package bead.egyszeru;

import java.sql.Timestamp;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

class GameStartException extends Exception {
    public int getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    private int socket;
    private String name;
    /**
     * Valakinek kezdenie kell a jatekot!
     * @param socket a kezdo jatekos socketjenek indexe
     * @param name a kezdo jatekos neve
     */
    public GameStartException(int socket, String name) {
        this.socket = socket;
        this.name = name;
    }
}
class WrongWordException extends Exception {}

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CopyOnWriteArrayList<Socket> allClients;
    private final CopyOnWriteArrayList<SzoJatek> jatekok;

    private SzoJatek jatek = null;
    private String myName = null;
    private int otherSocket = -1;
    private Timestamp startTime;

    public ClientHandler(Socket socket, CopyOnWriteArrayList<Socket> allClients, CopyOnWriteArrayList<SzoJatek> jatekok) {
        this.socket = socket;
        this.allClients = allClients;
        this.jatekok = jatekok;
        startTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public void publishMessage(String message) throws GameStartException {
        // keressuk meg a jatszotarsunkat
        int thisSocket = allClients.indexOf(this.socket);

        //Done: bevarni a 2. jatekost is!
        if(thisSocket % 2 == 1) {
            otherSocket = thisSocket -1;
        } else if(thisSocket + 1 < allClients.size()){
            otherSocket = thisSocket + 1;
        }

        if(myName == null){
            myName = message;
            System.out.print("Player " + myName + " enters the game...");
            if( (otherSocket != -1) && (thisSocket % 2 == 0) ) {
                System.out.println(" and found a peer");
                throw new GameStartException(thisSocket, myName);
            }else {
                System.out.printf(" and waits for a peer");
            }

        } else if (otherSocket >= 0){
            // milyen szot uzentunk legutobb?
            try (PrintStream out = new PrintStream(allClients.get(otherSocket).getOutputStream())){
                if(message.equals("nyert")){
                    out.println(message);
                    out.flush();
                    System.out.println(myName + " veszitett");
                }else {
                    if(this.jatek == null) {
                        this.jatek = this.jatekok.get(thisSocket/2);
                    }
                    if(!this.jatek.newMsg(myName, message)){
                        try(PrintStream me = new PrintStream(this.socket.getOutputStream())) {
                            me.println("Rossz szo, probald ujra!");
                            me.flush();
                        } catch (IOException e) {
                            System.out.println(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    } else {
                        out.println(myName + ": " + message);
                        out.flush();
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
                        publishMessage("nyert");
//                        allClients.remove(socket);
                        System.out.println("Player " + myName + " exits");
                        socket.close();
                        this.jatek.writeToFile(startTime);

                    } else if ((otherSocket != -1) && allClients.get(otherSocket).isClosed()) {
                            System.out.println("The other player exited, closing socket for player " + myName);
                            socket.close();
                    }

                    publishMessage(line);
                } catch (GameStartException e) {
                    //Todo: Start uzenetet kuldeni a kezdo jatekosnak
                    output.println("start");
                    output.flush();
                    System.out.println(e.getName() + ": starts the game");
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
