package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public class Weapon extends Treasure {

    public Weapon(Location location) {
        super(location);
    }

    public Weapon(Location location, int level) {
        super(location, level);
    }

    @Override
    public String toString() {
        return "Weapon: level - " + getLevel() + ", attack - " + getAttack();
    }
}
