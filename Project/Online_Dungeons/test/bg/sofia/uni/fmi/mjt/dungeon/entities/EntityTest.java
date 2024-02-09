package bg.sofia.uni.fmi.mjt.dungeon.entities;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityTest {
    Entity entity = new Entity(new Location(0, 1));

    @Test
    void testReceiveDamage() {
        entity.receiveDamage(20);
        assertEquals(80, entity.getCurrentHealth(),
            "It was expected that the entity will be left on 80 helath");
    }

    @Test
    void testChangeLocationWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> entity.changeLocation(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testChangeLocation() {
        entity.changeLocation(new Location(0,3));
        assertEquals(new Location(0,3), entity.getLocation(),
            "It was expected that the entity will move");
    }
}
