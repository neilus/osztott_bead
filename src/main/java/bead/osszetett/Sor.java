package bead.osszetett;

import java.io.Serializable;

public class Sor implements Serializable {
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
