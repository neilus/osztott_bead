package bead.egyszeru;

import java.sql.Timestamp;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.oracle.jrockit.jfr.ContentType.Timestamp;

class Sor implements Serializable{
    public String getName() {
        return name;
    }

    String name;
    String szo;
    public Sor(String name, String szo){
        this.name = name;
        this.szo = szo;
    }
    public Sor(){
        this.name = null;
        this.szo = null;
    }
    public String toString(){
        return this.name + " " + this.szo + "\n\r";
    }
}
public class SzoJatek implements Serializable{
    private List<Sor> jatek;

    public SzoJatek(){
        jatek = new CopyOnWriteArrayList<>();
    }

    public boolean newMsg(String name, String msg){
        if(jatek.size() < 1){
            jatek.add(new Sor(name, msg));
        } else {
            String szo = jatek.get(jatek.size() - 1).szo;

            if (msg.charAt(0) == szo.charAt(szo.length() -1)) {
                jatek.add(new Sor(name, msg));
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
