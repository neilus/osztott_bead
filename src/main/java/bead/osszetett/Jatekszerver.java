package bead.osszetett;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Jatekszerver {
    final static int PORT = 65456;
    final static int aMinute = 60000;
    public static void main(String[] args) throws IOException, NotBoundException, RemoteException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(4);
        final CopyOnWriteArrayList<Socket> sockets = new CopyOnWriteArrayList<>();
        final CopyOnWriteArrayList<SzoJatek> jatekok = new CopyOnWriteArrayList<>();
        final TiltottIface[] tiltottSzervers;
        final CopyOnWriteArrayList<Integer> tiltottSrv = new CopyOnWriteArrayList<>();

        Registry reg = LocateRegistry.getRegistry();
        System.out.println("Listing tiltott szavak services registered to the rmi service:");
        {
            int regnum = reg.list().length, i = 0;
            tiltottSzervers = new TiltottIface[regnum];
            for (String srv : reg.list()) {
                System.out.println("...looking up " + srv);
                tiltottSzervers[i++] = (TiltottIface) reg.lookup(srv);
            }
        }
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
                        jatekok,
                        tiltottSzervers,
                        tiltottSrv
                    )
                );
            }
        } catch (SocketTimeoutException timoutEx){
            System.out.println(timoutEx.getLocalizedMessage());
            int i;
            for(i = 0; i < jatekok.size() && jatekok.get(i).hasEnded(); i++)
                ;
            try {
                if (jatekok.get(i).hasEnded()) {
                    System.exit(0);
                }
            }catch(IndexOutOfBoundsException nogame){
                System.out.println("Total nr of games registered right now: " + nogame.getLocalizedMessage());
                System.exit(0);
            }
        } catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
    }
}