package bead.osszetett;

import java.io.*;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SzoJatek implements Serializable{
    private List<Sor> jatek;
    private boolean ended = false;

    public void endGame(){
        ended = true;
    }

    public boolean hasEnded(){
        return ended;
    }

    public SzoJatek(){
        jatek = new CopyOnWriteArrayList<>();
    }

    /**
     * Try to send a message int the game. It also has to qualify to the game rules to achieve this. If it qualifies
     * It will be added to the messages submitted in the game and return <b>true</b>, if not then simply returns false;
     * @param name players name
     * @param msg players message
     * @return false if the message doesn't qualifies
     */
    public boolean newMsg(String name, String msg, TiltottIface lista) throws RemoteException, TiltottSzoException {
        if(jatek.size() < 1){
            if(!lista.tiltottE(msg)) {
                jatek.add(new Sor(name, msg));

                return true;
            } else
                throw new TiltottSzoException(msg);
        } else {
            String szo = jatek.get(jatek.size() - 1).szo;

            if (msg.charAt(0) == szo.charAt(szo.length() -1)) {
                if(!lista.tiltottE(msg)) {
                    jatek.add(new Sor(name, msg));

                    return true;
                } else
                    throw new TiltottSzoException(msg);
            } else
                return false;
        }

        return true;
    }

    ///Done: Idobelyeggel ellatott jatek-log, soronkent: <jatekos neve> <szo>
    public void writeToFile(Timestamp startTime){
        try (
                ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(new File(
                        jatek.get(0).getName() + "_"
                        + jatek.get(1).getName() + "_"
                        + startTime.toString() + ".txt"


                )));
                PrintWriter printWriter = new PrintWriter(file);
        ){
            printWriter.write("\n");
            for(Sor sor: jatek){
                printWriter.write(sor.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
