package bead.osszetett;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by enorsan on 6/2/16.
 */
public class TiltottImpl extends UnicastRemoteObject implements TiltottIface{
    List<String> tiltottSzavak = new CopyOnWriteArrayList<>();
    protected TiltottImpl() throws RemoteException {
    }

    protected TiltottImpl(int i) throws RemoteException, FileNotFoundException {
        BufferedReader szavasFile = new BufferedReader(new FileReader("./src/main/resources/szokincs" + i + ".txt"));
        String line = null;
        try {
            line = szavasFile.readLine();
            while(line != null){
                tiltottSzavak.add(line);
                line = szavasFile.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    protected TiltottImpl(int i, RMIClientSocketFactory rmiClientSocketFactory, RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        super(i, rmiClientSocketFactory, rmiServerSocketFactory);
    }

    @Override
    public boolean tiltottE(String ujSzo) throws RemoteException {
        return false;
    }
}
