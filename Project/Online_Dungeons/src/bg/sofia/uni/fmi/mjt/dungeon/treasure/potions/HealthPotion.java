package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;

public class HealthPotion extends Spell {

    public HealthPotion(Location location) {
        super(location);
        setManaCost(0);
    }

    public HealthPotion(Location location, int level) {
        super(location, level);
        setManaCost(0);
    }

    @Override
    public String toString() {
        return "HealthPotion: level - " + getLevel() + ", attack - " + getAttack() + ", manaCost - " + getManaCost();
    }
}
