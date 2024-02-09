package bg.sofia.uni.fmi.mjt.dungeon.entities.minions;

public interface MinionAPI {

    /**
     * @return random amount of xp which is dropped
     * [level, level*2], where level is the level of the minion
     */
    int droppedXp();
}
