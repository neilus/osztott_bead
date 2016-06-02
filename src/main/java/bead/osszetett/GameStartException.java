package bead.osszetett;

public class GameStartException extends Exception {
    public int getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    private int socket;
    private String name;
    /**
     * Valakinek kezdenie kell a jatekot!
     * @param socket a kezdo jatekos socketjenek indexe
     * @param name a kezdo jatekos neve
     */
    public GameStartException(int socket, String name) {
        this.socket = socket;
        this.name = name;
    }
}
