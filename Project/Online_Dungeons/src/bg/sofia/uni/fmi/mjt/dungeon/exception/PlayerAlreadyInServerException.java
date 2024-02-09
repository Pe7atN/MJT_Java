package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class PlayerAlreadyInServerException extends Exception {
    public PlayerAlreadyInServerException(String message) {
        super(message);
    }

    public PlayerAlreadyInServerException(String message, Throwable t) {
        super(message, t);
    }
}
