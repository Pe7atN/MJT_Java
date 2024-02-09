package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class BackPackIsFullException extends Exception {
    public BackPackIsFullException(String message) {
        super(message);
    }

    public BackPackIsFullException(String message, Throwable t) {
        super(message, t);
    }
}
