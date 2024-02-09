package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public class Spell extends Treasure {

    private int manaCost; //The manaCost is between [level*5, level * 6], and its random
    private static final int MIN_MANA_COST = 5;
    private static final int MAX_MANA_COST = 6;

    public Spell(Location location) {
        super(location);
        setManaCost((int) (Math.random() + MIN_MANA_COST));
    }

    public Spell(Location location, int level) {
        super(location, level);
        int minCost = level * MIN_MANA_COST;
        int maxCost = level * MAX_MANA_COST;
        setManaCost((int) (Math.random() * (maxCost - minCost) + minCost));
    }

    public int getManaCost() {
        return manaCost;
    }

    //this is protected because it is only used in Potions
    protected void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    @Override
    public String toString() {
        return "Spell: level - " + getLevel() + ", attack - " + getAttack() + ", manaCost - " + getManaCost();
    }
}
