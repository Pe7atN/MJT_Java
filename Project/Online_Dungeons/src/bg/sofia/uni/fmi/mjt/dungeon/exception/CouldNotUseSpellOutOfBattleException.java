package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class CouldNotUseSpellOutOfBattleException extends Exception {
    public CouldNotUseSpellOutOfBattleException(String message) {
        super(message);
    }

    public CouldNotUseSpellOutOfBattleException(String message, Throwable t) {
        super(message, t);
    }
}
