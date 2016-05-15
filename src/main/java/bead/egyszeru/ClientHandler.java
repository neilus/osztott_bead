package bead.egyszeru;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;


public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CopyOnWriteArrayList<Socket> allClients;
    private final CopyOnWriteArrayList<String> allNames;

    public ClientHandler(Socket socket, CopyOnWriteArrayList<Socket> allClients, CopyOnWriteArrayList<String> allNames) {
        this.socket = socket;
        this.allClients = allClients;
        this.allNames = allNames;
    }

    public void publishMessage(String message) {
        // keressuk meg a jatszotarsunkat
        int otherSocket = -1;
        int thisSocket = allClients.indexOf(this.socket);

        if(thisSocket % 2 == 1) {
            otherSocket = thisSocket -1;
        } else if(thisSocket + 1 < allClients.size()){
            otherSocket = thisSocket + 1;
        }

        // milyen szot uzentunk legutobb?
        try {
            PrintStream out = new PrintStream(
                    allClients.get(otherSocket).getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
                if ("exit".equals(line)) {
                    allClients.remove(socket);
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
