package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;

public interface TreasureAPI {

    /**
     * @return the location of the treasure (weapon or spell)
     */
    Location getLocation();

    /**
     * @return the level of the treasure (weapon or spell)
     */
    int getLevel();

    /**
     * @return the attack of the treasure (weapon or spell)
     */
    int getAttack();
}
