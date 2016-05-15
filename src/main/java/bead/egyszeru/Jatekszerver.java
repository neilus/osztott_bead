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
        final CopyOnWriteArrayList<SzoJatek> jatekok = new CopyOnWriteArrayList<>();

        try {
            //Done: PORT = 65456
            ServerSocket serverSocket = new ServerSocket(PORT);



            //Todo: Egyszvas uzenetet fogad a jatekostol
            //Todo: Folytassa a szolancot, stb
            //Todo: Server timeout 60sec

            //Done: Ciklusban varja a jatekosokat

            //Done: Tobb parhuzamos jatekot is tudjon kiszolgalni
            while (true) {
                threadPool.execute(
                    new ClientHandler(
                        serverSocket.accept(),
                        sockets,
                        jatekok
                    )
                );


            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}