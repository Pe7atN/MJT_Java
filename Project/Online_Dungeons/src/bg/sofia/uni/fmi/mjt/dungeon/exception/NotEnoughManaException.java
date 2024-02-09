package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class NotEnoughManaException extends Exception {
    public NotEnoughManaException(String message) {
        super(message);
    }

    public NotEnoughManaException(String message, Throwable t) {
        super(message, t);
    }
}
