package bead.osszetett;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter playmate = null, me;

    private final CopyOnWriteArrayList<Socket> allClients;
    private final CopyOnWriteArrayList<SzoJatek> jatekok;
    private final TiltottIface[] tiltottIfaces;
    private TiltottIface tiltottSrv;

    private SzoJatek jatek = null;
    private String myName = null;
    private int otherSocket = -1;
    private Timestamp startTime;

    public ClientHandler(Socket socket,
                         CopyOnWriteArrayList<Socket> allClients,
                         CopyOnWriteArrayList<SzoJatek> jatekok,
                         TiltottIface[] tiltottSzervers) {

        this.tiltottIfaces = tiltottSzervers;
        this.socket = socket;
        this.allClients = allClients;
        this.jatekok = jatekok;
        startTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public void publishMessage(String message, PrintWriter me, PrintWriter playmate) throws GameStartException {
        // keressuk meg a jatszotarsunkat
        int thisSocket = allClients.indexOf(this.socket);

        //Done: bevarni a 2. jatekost is!
        if(thisSocket % 2 == 1) {
            otherSocket = thisSocket -1;
            tiltottSrv = tiltottIfaces[0];
        } else if(thisSocket + 1 < allClients.size()){
            otherSocket = thisSocket + 1;
            tiltottSrv = tiltottIfaces[1];
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
                    if(!this.jatek.newMsg(myName, message, tiltottSrv)){
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

    public void run() {
        allClients.add(socket);

        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream());
        ){
            while (!socket.isClosed()) {
                String line = input.readLine();
                //Done: Exit-re kilep a jatekbol, a masik jatekos nyert, bontja mindket kapcsolatot
                try {
                    if ("exit".equals(line)) {
                        publishMessage("nyert", me, playmate);
//                        allClients.remove(socket);
                        System.out.println("Player " + myName + " exits");
                        socket.close();
                        this.jatek.writeToFile(startTime);

                    } else if ((otherSocket != -1) && allClients.get(otherSocket).isClosed()) {
                            System.out.println("The other player exited, closing socket for player " + myName);
                            output.println();
                            socket.close();
                    }

                    publishMessage(line, me, playmate);
                } catch (GameStartException e) {
                    //Todo: Start uzenetet kuldeni a kezdo jatekosnak
                    if(playmate == null){
                        playmate = new PrintWriter(allClients.get(otherSocket).getOutputStream());
                    }
                    playmate.println("start");
                    playmate.flush();
                    System.out.println("Starting the game...");
                    this.jatek = new SzoJatek();
                    this.jatekok.add(this.jatek);
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
