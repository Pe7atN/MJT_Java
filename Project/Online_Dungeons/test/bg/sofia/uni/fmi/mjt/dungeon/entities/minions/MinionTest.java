package bg.sofia.uni.fmi.mjt.dungeon.entities.minions;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinionTest {
    Minion minion = new Minion(new Location(0,0),3);

    @Test
    void testDroppedXpByMinion() {
        int droppedXp = minion.droppedXp();
        assertTrue(droppedXp >= 3, "It was expected to be between 3 and 6");
        assertTrue(droppedXp <= 6, "It was expected to be between 3 and 6");
    }
}
