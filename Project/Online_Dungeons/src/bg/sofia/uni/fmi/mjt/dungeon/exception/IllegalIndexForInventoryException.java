package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class IllegalIndexForInventoryException extends Exception {
    public IllegalIndexForInventoryException(String message) {
        super(message);
    }

    public IllegalIndexForInventoryException(String message, Throwable t) {
        super(message, t);
    }
}
