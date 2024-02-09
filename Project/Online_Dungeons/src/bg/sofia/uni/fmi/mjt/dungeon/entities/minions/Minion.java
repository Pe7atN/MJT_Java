package bg.sofia.uni.fmi.mjt.dungeon.entities.minions;

import bg.sofia.uni.fmi.mjt.dungeon.entities.Entity;
import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public class Minion extends Entity implements MinionAPI {

    public Minion(Location location, int level) {
        super(location, level);
    }

    @Override
    public int droppedXp() {
        // as the XP which can be dropped is from [currentLevel, currentLevel * 2]
        return (int) (Math.random() * (getLevel() + 1) + getLevel());
    }
}
