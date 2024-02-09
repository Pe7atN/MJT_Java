package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public class Treasure implements TreasureAPI {

    private Location location;
    private int level;
    private int attack; //The attack is between [((level-1)*3 + 1), level * 3], and its random

    private static final int ATTACK_PER_LEVEL = 3;

    public Treasure(Location location) {
        this.location = location;
        this.level = 1;
        this.attack = (int) (Math.random() * ATTACK_PER_LEVEL + 1);
    }

    public Treasure(Location location, int level) {
        this.location = location;
        this.level = level;
        this.attack = (int) (Math.random() * ATTACK_PER_LEVEL + ((level - 1) * ATTACK_PER_LEVEL + 1));
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public String toString() {
        return "Treasure: level - " + getLevel() + ", attack - " + getAttack();
    }

}
