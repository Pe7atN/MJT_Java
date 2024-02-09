package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class CouldOnlyCastASpellException extends Exception {
    public CouldOnlyCastASpellException(String message) {
        super(message);
    }

    public CouldOnlyCastASpellException(String message, Throwable t) {
        super(message, t);
    }
}
