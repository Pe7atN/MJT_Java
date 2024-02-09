package bg.sofia.uni.fmi.mjt.dungeon.entities;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public class Entity implements Actor {

    private Location location;
    private Stats stats;
    private int level;
    private int currentHealth;

    private static final int START_HEALTH_AND_MANA = 100;
    private static final int START_ATTACK_AND_DEFENSE = 50;

    private static final int HEALTH_AND_MANA_PER_LEVEL = 10;
    private static final int ATTACK_AND_DEFENSE_PER_LEVEL = 5;

    public Entity(Location location) {
        this.location = location;
        this.level = 1;
        this.stats =
            new Stats(START_HEALTH_AND_MANA, START_HEALTH_AND_MANA, START_ATTACK_AND_DEFENSE, START_ATTACK_AND_DEFENSE);
        currentHealth = stats.health();
    }

    public Entity(Location location, int level) {
        this.location = location;
        this.level = level;

        int additionalHealthAndMana = (level - 1) * HEALTH_AND_MANA_PER_LEVEL;
        int additionalAttackAndDefense = (level - 1) * ATTACK_AND_DEFENSE_PER_LEVEL;
        this.stats =
            new Stats(START_HEALTH_AND_MANA + additionalHealthAndMana,
                START_HEALTH_AND_MANA + additionalHealthAndMana,
                START_ATTACK_AND_DEFENSE + additionalAttackAndDefense,
                START_ATTACK_AND_DEFENSE + additionalAttackAndDefense);
        currentHealth = stats.health();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void updateStats() {
        stats = new Stats(stats.health() + HEALTH_AND_MANA_PER_LEVEL,
            stats.mana() + HEALTH_AND_MANA_PER_LEVEL,
            stats.attack() + ATTACK_AND_DEFENSE_PER_LEVEL,
            stats.defense() + ATTACK_AND_DEFENSE_PER_LEVEL);
    }

    @Override
    public void changeLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        this.location = location;
    }

    @Override
    public void levelUp() {
        level++;
    }

    @Override
    public void receiveDamage(int damage) {
        setCurrentHealth(getCurrentHealth() - damage);
    }

}
