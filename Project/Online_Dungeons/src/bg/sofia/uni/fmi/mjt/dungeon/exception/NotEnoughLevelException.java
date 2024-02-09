package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class NotEnoughLevelException extends Exception {
    public NotEnoughLevelException(String message) {
        super(message);
    }

    public NotEnoughLevelException(String message, Throwable t) {
        super(message, t);
    }
}
