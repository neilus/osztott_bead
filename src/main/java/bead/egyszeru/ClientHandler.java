package bead.egyszeru;

import java.sql.Timestamp;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;


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
                try {
                    //Done: Start uzenetet kuldeni a kezdo jatekosnak
                    PrintStream out = new PrintStream(
                            allClients.get(otherSocket).getOutputStream());
                    out.println("start");
                    System.out.println(myName + ": starts the game");
                    this.jatek = new SzoJatek();
                    this.jatekok.add(this.jatek);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (otherSocket >= 0){
            // milyen szot uzentunk legutobb?
            try {
                PrintStream out = new PrintStream(
                        allClients.get(otherSocket).getOutputStream());
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
                            me.println("Rosz szo, probald ujra!");
                            me.flush();
                        }
                    } else {
                        out.println(myName + ": " + message);
                        out.flush();
                        System.out.println(myName + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(2000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        allClients.add(socket);


        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            while (true) {
                String line = br.readLine();
                //Done: Exit-re kilep a jatekbol, a masik jatekos nyert, bontja mindket kapcsolatot
                if ("exit".equals(line)) {
                    publishMessage("nyert");
//                    allClients.remove(socket);
                    socket.close();
                    this.jatek.writeToFile(startTime);
                    return;
                }
                if( (otherSocket != -1) && (allClients.get(otherSocket).isClosed()) ){
                    socket.close();

                    return;
                }
                publishMessage(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
