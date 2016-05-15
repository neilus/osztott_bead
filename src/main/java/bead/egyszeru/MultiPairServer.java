package bead.egyszeru;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by neilus on 5/15/16.
 */
public class MultiPairServer {
    public static void main(String[] args) {
        final ExecutorService pool =
                Executors.newFixedThreadPool(16);
        final CopyOnWriteArrayList<Socket> sockets =
                new CopyOnWriteArrayList<Socket>();
        try {
            ServerSocket serverSock = new ServerSocket(5656);
            while (true) {
                pool.execute(new ClientHandler(
                        serverSock.accept(),
                        sockets));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
