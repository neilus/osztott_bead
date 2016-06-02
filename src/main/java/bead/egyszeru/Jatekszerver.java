package bead.egyszeru;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Jatekszerver {
    final static int PORT = 65456;
    final static int aMinute = 60000;
    public static void main(String[] args) throws IOException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(4);
        final CopyOnWriteArrayList<Socket> sockets = new CopyOnWriteArrayList<>();
        final CopyOnWriteArrayList<SzoJatek> jatekok = new CopyOnWriteArrayList<>();

        try {
            //Done: PORT = 65456
            ServerSocket serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(aMinute);

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
        } catch (SocketTimeoutException timoutEx){
            System.out.println(timoutEx.getLocalizedMessage());
            int i;
            for(i = 0; i < jatekok.size() && jatekok.get(i).hasEnded(); i++)
                ;
            if(jatekok.get(i).hasEnded()) {
                System.exit(0);
            }
        } catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }
}