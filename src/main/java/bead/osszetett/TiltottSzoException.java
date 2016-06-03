package bead.osszetett;

/**
 * Created by enorsan on 6/3/16.
 */
public class TiltottSzoException extends Exception {
    private String szo;

    public TiltottSzoException(String szo) {
        this.szo = szo;
    }

    public String getSzo() {
        return szo;
    }
}
