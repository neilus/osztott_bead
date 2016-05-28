package bead.egyszeru;

import java.sql.Timestamp;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

class GameStartException extends Exception {}
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

    public void publishMessage(String message) {
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
            if( (otherSocket != -1) && (thisSocket % 2 == 0) ) {
                throw new GameStartException();
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
                System.out.println(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        try {
//            Thread.sleep(2000);
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        allClients.add(socket);

        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream output = new PrintStream(socket.getOutputStream());
        ){
            while (true) {
                String line = input.readLine();
                //Done: Exit-re kilep a jatekbol, a masik jatekos nyert, bontja mindket kapcsolatot
                if ("exit".equals(line)) {
                    publishMessage("nyert");
//                    allClients.remove(socket);
                    socket.close();
                    this.jatek.writeToFile(startTime);

                    break;
                }
                if( (otherSocket != -1) && (allClients.get(otherSocket).isClosed()) ){
                    socket.close();

                    break;
                }
                try {
                    publishMessage(line);
                }catch (GameStartException e) {
                    //Done: Start uzenetet kuldeni a kezdo jatekosnak
                    output.write("start");
                    System.out.println(myName + ": starts the game");
                    this.jatek = new SzoJatek();
                    this.jatekok.add(this.jatek);
                    output.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
