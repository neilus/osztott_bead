package bead.osszetett;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Integer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter playmate = null, me;

    private final CopyOnWriteArrayList<Socket> allClients;
    private final CopyOnWriteArrayList<SzoJatek> jatekok;
    private final TiltottIface[] tiltottIfaces;
    private final CopyOnWriteArrayList<Integer> tiltottSrv;

    private SzoJatek jatek = null;
    private String myName = null;
    private int otherSocket = -1;
    private Timestamp startTime;

    public ClientHandler(Socket socket,
                         CopyOnWriteArrayList<Socket> allClients,
                         CopyOnWriteArrayList<SzoJatek> jatekok,
                         TiltottIface[] tiltottSzervers,
                         CopyOnWriteArrayList<Integer> tiltottSrv) {

        this.tiltottIfaces = tiltottSzervers;
        this.tiltottSrv = tiltottSrv;
        this.socket = socket;
        this.allClients = allClients;
        this.jatekok = jatekok;
        startTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public void publishMessage(String message, PrintWriter me, PrintWriter playmate) throws GameStartException, TiltottSzoException {
        // keressuk meg a jatszotarsunkat
        int thisSocket = allClients.indexOf(this.socket);

        //Done: bevarni a 2. jatekost is!
        if(thisSocket % 2 == 1) {
            otherSocket = thisSocket -1;
            if(tiltottSrv.get(thisSocket) == -1)
                tiltottSrv.set(thisSocket, 0);
        } else if(thisSocket + 1 < allClients.size()){
            otherSocket = thisSocket + 1;
            if(tiltottSrv.get(thisSocket) == -1)
                tiltottSrv.set(thisSocket, 1);
        }

        if(myName == null){
            myName = message;
            System.out.print("Player " + myName + " enters the game...");
            if(thisSocket % 2 == 1){
                System.out.println(" and found a peer");
                throw new GameStartException(thisSocket, myName);
            }else {
                System.out.println(" and waits for a peer");
            }

        } else if (otherSocket >= 0){
            // milyen szot uzentunk legutobb?
            try {
                if(playmate == null) {
                    playmate = new PrintWriter(allClients.get(otherSocket).getOutputStream());
                }

                if(message.equals("nyert")){
                    this.jatek.endGame();
                    playmate.println(message);
                    playmate.flush();
                    System.out.println(myName + " veszitett");
                }else {
                    if(this.jatek == null) {
                        this.jatek = this.jatekok.get(thisSocket/2);
                    }
                    if(!this.jatek.newMsg(myName, message, tiltottIfaces[tiltottSrv.get(thisSocket)])){
                        if(me == null) {
                            me = new PrintWriter(socket.getOutputStream());
                        }
                        me.println("Rossz szo, probald ujra!");
                        me.flush();
                    } else {
                        playmate.println(myName + ": " + message);
                        playmate.flush();
                        System.out.println(myName + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Valaki bezarta a socketet ido elott! " + e.getLocalizedMessage());
            }
        }
    }

    private void msgMate(String msg) throws IOException {
        if (playmate == null) {
            playmate = new PrintWriter(allClients.get(otherSocket).getOutputStream());
        }
        playmate.println(msg);
        playmate.flush();
    }

    private void msgMe(String msg) throws IOException {
        if (me == null) {
            me = new PrintWriter(socket.getOutputStream());
        }
        me.println(msg);
        me.flush();
    }

    public void run() {
        allClients.add(socket);
        tiltottSrv.add(-1);

        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream());
        ){
            while (!socket.isClosed()) {
                String line = input.readLine();
                //Done: Exit-re kilep a jatekbol, a masik jatekos nyert, bontja mindket kapcsolatot
                try {
                    if ("exit".equals(line)) {
                        System.out.println("Player " + myName + " exits");
                        throw new TiltottSzoException(line);
                    } else if ((otherSocket != -1) && allClients.get(otherSocket).isClosed()) {
                        System.out.println("The other player exited, closing socket for player " + myName);
                        output.println();
                        socket.close();
                        throw new GameEndException();
                    } else if(line.contains("tiltott")){
                        throw new TiltottSzerverException(line);
                    }

                    publishMessage(line, me, playmate);

                } catch (GameStartException e) {
                    //Todo: Start uzenetet kuldeni a kezdo jatekosnak
                    System.out.println("Starting the game...");
                    msgMate("start");
                    this.jatek = new SzoJatek();
                    this.jatekok.add(this.jatek);

                } catch (TiltottSzoException e) {
                    System.out.println(myName + " egy tiltott szoval probalkozott: " + e.getSzo());
                    this.jatek.endGame();

                    msgMate("nyert");
                    allClients.get(otherSocket).close();

                    msgMe("Tiltott szo, vesztettel!");
                    socket.close();

                    this.jatek.writeToFile(startTime);
                } catch (IOException ex){
                    System.out.println("Game Over, " + ex.getLocalizedMessage());
                } catch (GameEndException e) {
                    System.out.println("Game Over, " + myName + " Wins!");
                } catch (TiltottSzerverException e) {
                    if(tiltottIfaces.length > e.getSrvnum()) {
                        tiltottSrv.set(otherSocket, e.getSrvnum());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ne ezt nem kene elerni " + e.getLocalizedMessage());
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("Ezt meg plane nem kene! " + e1.getLocalizedMessage());
            }
        }
    }
}
