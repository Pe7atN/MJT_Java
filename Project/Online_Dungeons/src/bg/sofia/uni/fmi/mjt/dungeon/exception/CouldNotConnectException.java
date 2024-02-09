package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class CouldNotConnectException extends Exception {
    public CouldNotConnectException(String message) {
        super(message);
    }

    public CouldNotConnectException(String message, Throwable t) {
        super(message, t);
    }
}
