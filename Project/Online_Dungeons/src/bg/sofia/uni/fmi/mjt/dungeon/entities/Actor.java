package bg.sofia.uni.fmi.mjt.dungeon.entities;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public interface Actor {

    /**
     * @return the current location of the entity (hero or minion)
     */
    Location getLocation();

    /**
     * @return the stats of the entity (hero or minion)
     */
    Stats getStats();

    /**
     * @return the current health of the hero
     */
    int getCurrentHealth();

    /**
     * @return the level of the entity (hero or minion)
     */
    int getLevel();

    /**
     * It changes the current health of the entity
     *
     * @param currentHealth the new current health of the entity
     */
    void setCurrentHealth(int currentHealth);

    /**
     * It increases the stats of the entity
     */
    void updateStats();

    /**
     * It changes the location of the entity with location
     *
     * @param location the new location of the entity
     * @throws IllegalArgumentException if location is null
     */
    void changeLocation(Location location);

    /**
     * It increases the level of the entity with one
     */
    void levelUp();

    /**
     * The hero takes a certain damage, which decreases his current health
     *
     * @param damage the damage the hero takes
     */
    void receiveDamage(int damage);
}
