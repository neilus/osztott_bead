package bead.osszetett;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by enorsan on 6/2/16.
 */
public class TiltottDeploy {

    public static void main(String[] args) throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        int count = 2;
//		 saj�t registry

        if(args.length > 1){
            count = Integer.parseInt(args[0]);
        }

        TiltottImpl[] tiltottImpls = new TiltottImpl[count + 1];
        for(int i = 1; i <= count; i++){
            tiltottImpls[i] = new TiltottImpl(i);
            System.out.println("Binding tiltott" + i + " to the RMI registry service...");
            reg.bind("tiltott" + i, tiltottImpls[i]);
        }
    }
}
