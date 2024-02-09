package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;

public class ManaPotion extends Spell {

    public ManaPotion(Location location) {
        super(location);
        setManaCost(0);
    }

    public ManaPotion(Location location, int level) {
        super(location, level);
        setManaCost(0);
    }

    @Override
    public String toString() {
        return "ManaPotion: level - " + getLevel() + ", attack - " + getAttack() + ", manaCost - " + getManaCost();
    }
}
