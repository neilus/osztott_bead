package bead.osszetett;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by enorsan on 6/3/16.
 */
public class StartRMIRegistryService {
    public static void main(String[] args) throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(1099);
        while(true)
            ;
    }
}
