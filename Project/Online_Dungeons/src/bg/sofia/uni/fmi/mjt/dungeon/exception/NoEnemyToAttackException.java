package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class NoEnemyToAttackException extends Exception {
    public NoEnemyToAttackException(String message) {
        super(message);
    }

    public NoEnemyToAttackException(String message, Throwable t) {
        super(message, t);
    }
}
