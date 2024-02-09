package bg.sofia.uni.fmi.mjt.dungeon.map;

import bg.sofia.uni.fmi.mjt.dungeon.entities.heroes.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.entities.minions.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.exception.PlayerAlreadyInServerException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.util.Map;

public interface DungeonAPI {

    /**
     * @return map of locations where there is a treasure
     */
    Map<Location, Treasure> getTreasures();

    /**
     * @return map of locations where there is a minion
     */
    Map<Location, Minion> getMinions();

    /**
     * Print the dungeon of the game
     */
    void printDungeon();

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a minion
     * @throws IllegalArgumentException if the location is null
     */
    boolean isMinion(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a hero
     * @throws IllegalArgumentException if the location is null
     */
    boolean isHero(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is another hero
     * @throws IllegalArgumentException if the location is null
     */
    boolean isAnotherHero(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a minion
     * or another hero
     * @throws IllegalArgumentException if the location is null
     */
    boolean isAttackable(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a treasure
     * @throws IllegalArgumentException if the location is null
     */
    boolean isTreasure(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a wall
     * @throws IllegalArgumentException if the location is null
     */
    boolean isWall(Location location);

    /**
     * @param location the location which is considered
     * @return true if on the certain location there is a position
     * where a hero can step or go
     * @throws IllegalArgumentException if the location is null
     */
    boolean isValidPosition(Location location);

    /**
     * @param location the location which is considered
     * @return true if the certain location is free
     * @throws IllegalArgumentException if the location is null
     */
    boolean isFreeSpace(Location location);

    /**
     * Frees certain location
     *
     * @param location the location which is considered
     * @throws IllegalArgumentException if the location is null
     */
    void freeCertainSpace(Location location);

    /**
     * @param location the location which is considered
     * @param hero     the hero which is also on the same location
     * @return the other hero on the same location, apart from
     * the hero which is given
     * @throws IllegalArgumentException if the location or hero is null
     */
    Hero getAnotherHero(Location location, Hero hero);

    /**
     * Removes a minion from certain location
     *
     * @param location the location which is considered
     * @throws IllegalArgumentException if the location is null
     */
    void removeMinionFromLocation(Location location);

    /**
     * Removes a treasure from certain location
     *
     * @param location the location which is considered
     * @throws IllegalArgumentException if the location is null
     */
    void removeTreasureFromLocation(Location location);

    /**
     * It moves a hero from certain position to another
     *
     * @throws IllegalArgumentException if the hero, from or to is null
     */
    void moveHero(Hero hero, Location from, Location to);

    /**
     * It adds a new hero to the dungeon on a random free location
     *
     * @throws IllegalArgumentException       if the hero is null
     * @throws PlayerAlreadyInServerException if the same hero (by index) is in the dungeon
     */
    void addHero(Hero hero) throws PlayerAlreadyInServerException;

    /**
     * It removes a hero to the dungeon
     *
     * @throws IllegalArgumentException       if the hero is null
     */
    void removeHero(Hero hero);

    /**
     * It adds a new minion to the dungeon on a random free location. And the level
     * of the minion is the average level of the heroes in the dungeon
     */
    void placeNewMinion();

    /**
     * It adds a new treasure to the dungeon on a random free location. And the level
     * of the treasure is the average level of the heroes in the dungeon
     */
    void placeNewTreasure();
}
