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
//        Registry reg = LocateRegistry.createRegistry(4321);

        if(args.length > 1){
            count = Integer.parseInt(args[0]);
        }

        TiltottSzerver[] tiltottSzervers = new TiltottSzerver[count + 1];
        for(int i = 1; i <= count; i++){
            tiltottSzervers[i] = new TiltottSzerver(i);
            reg.bind("tiltott" + i, tiltottSzervers[i]);
        }
    }
}
