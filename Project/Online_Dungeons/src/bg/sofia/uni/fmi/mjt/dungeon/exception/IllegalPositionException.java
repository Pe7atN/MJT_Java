package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class IllegalPositionException extends Exception {
    public IllegalPositionException(String message) {
        super(message);
    }

    public IllegalPositionException(String message, Throwable t) {
        super(message, t);
    }
}
