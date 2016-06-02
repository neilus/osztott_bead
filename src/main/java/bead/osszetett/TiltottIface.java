package bead.osszetett;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by enorsan on 6/3/16.
 */
public interface TiltottIface extends Remote{
    public boolean tiltottE(String ujSzo) throws RemoteException;
}
