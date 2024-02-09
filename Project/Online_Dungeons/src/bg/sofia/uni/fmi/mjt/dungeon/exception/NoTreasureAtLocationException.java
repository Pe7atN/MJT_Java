package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class NoTreasureAtLocationException extends Exception {
    public NoTreasureAtLocationException(String message) {
        super(message);
    }

    public NoTreasureAtLocationException(String message, Throwable t) {
        super(message, t);
    }
}
