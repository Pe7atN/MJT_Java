package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TreasureTest {

    Treasure treasure = new Treasure(new Location(0,0), 2);
    Treasure treasureFirstLevel = new Treasure(new Location(1,0));

    @Test
    void testGetLevel(){
        assertEquals(2, treasure.getLevel(),
            "It is not the expected level");
    }

    @Test
    void testGetAttack(){
        assertTrue(treasure.getAttack() >= 4, "It was expected to be between 4 and 6");
        assertTrue(treasure.getAttack() <= 6, "It was expected to be between 4 and 6");
    }

    @Test
    void testToString(){
        int attack = treasure.getAttack();
        assertEquals("Treasure: level - 2, attack - " + attack, treasure.toString(),
            "It is not the expected level");
    }
}
