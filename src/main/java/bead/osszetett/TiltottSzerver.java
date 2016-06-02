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
public class TiltottSzerver extends UnicastRemoteObject {
    List<String> tiltottSzavak = new CopyOnWriteArrayList<>();
    protected TiltottSzerver() throws RemoteException {
    }

    protected TiltottSzerver(int i) throws RemoteException, FileNotFoundException {
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

    protected TiltottSzerver(int i, RMIClientSocketFactory rmiClientSocketFactory, RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        super(i, rmiClientSocketFactory, rmiServerSocketFactory);
    }

    public synchronized boolean tiltottE(String ujSzo){
        for(String szo:tiltottSzavak){
            if(ujSzo.equalsIgnoreCase(szo)){

                return true;
            }
        }
        tiltottSzavak.add(ujSzo);

        return false;
    }
}
