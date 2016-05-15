package bead.egyszeru;

import bead.egyszeru.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CopyOnWriteArrayList;



public class Jatekszerver {
    final static int PORT = 65456;

    public static void main(String[] args) throws IOException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(4);
        final CopyOnWriteArrayList<Socket> sockets = new CopyOnWriteArrayList<>();

        try {
            //Done: PORT = 65456
            ServerSocket serverSocket = new ServerSocket(PORT);
            //Todo: Exit-re kilep a jatekbol, a masik jatekos nyert, bontja mindket kapcsolatot
            //Todo: Idobelyeggel ellatott jatek-log, soronkent: <jatekos neve> <szo>
            //Todo: Start uzenetet kuldeni a kezdo jatekosnak
            //Todo: Egyszvas uzenetet fogad a jatekostol
            //Todo: Folytassa a szolancot, stb
            //Todo: Server timeout 60sec

            //Todo: Ciklusban varja a jatekosokat
            while (true) {
                //Todo: Tobb parhuzamos jatekot is tudjon kiszolgalni
                //ha accept -> new thread(socket) -> Jatek
                while (true) {
                    threadPool.execute(
                        new ClientHandler(
                            serverSocket.accept(),
                            sockets
                        )
                    );
                    //Todo: bevarni a 2. jatekost is!

                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}