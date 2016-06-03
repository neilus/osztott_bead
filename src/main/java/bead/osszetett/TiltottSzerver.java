package bead.osszetett;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by enorsan on 6/2/16.
 */
public class TiltottSzerver extends UnicastRemoteObject implements TiltottIface{
    List<String> tiltottSzavak = new CopyOnWriteArrayList<>();
    protected TiltottSzerver() throws RemoteException {
    }

    protected TiltottSzerver(int i) throws RemoteException, FileNotFoundException {
        BufferedReader szavasFile = new BufferedReader(new FileReader("./src/main/resources/szokincs" + i + ".txt"));
        String line = null;
        try {
            line = szavasFile.readLine();
            while(line != null){
                System.out.println("Adding word: " + line + " to the forbidden words list on server #" + i);
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

    @Override
    public boolean tiltottE(String ujSzo) throws RemoteException {
        for(String szo:tiltottSzavak){
            if(szo.equalsIgnoreCase(ujSzo)){
                System.out.println(ujSzo + " egy tiltott szo");
                return true;
            }
        }
        System.out.println(ujSzo + "felvetele a listaba");
        tiltottSzavak.add(ujSzo);

        return false;
    }
}
