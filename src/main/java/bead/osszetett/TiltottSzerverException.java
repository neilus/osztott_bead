package bead.osszetett;

/**
 * Created by enorsan on 6/3/16.
 */
public class TiltottSzerverException extends Throwable {
    private int srvnum;
    public TiltottSzerverException(String line) {
        System.out.println(line);
        System.out.println(line.substring(7).trim());
        srvnum = Integer.parseInt(line.substring(7).trim()) - 1;
        System.out.println(srvnum);
    }

    public int getSrvnum() {
        return srvnum;
    }
}
