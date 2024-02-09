package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class DeadPlayerCouldNotActException extends Exception {
    public DeadPlayerCouldNotActException(String message) {
        super(message);
    }

    public DeadPlayerCouldNotActException(String message, Throwable t) {
        super(message, t);
    }
}
