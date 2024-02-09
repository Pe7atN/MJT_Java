package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthPotionTest {
    HealthPotion healthPotion = new HealthPotion(new Location(0, 0), 2);
    HealthPotion healthPotionTest = new HealthPotion(new Location(1, 0));

    @Test
    void testToString() {
        int attack = healthPotion.getAttack();
        assertEquals("HealthPotion: level - 2, attack - " + attack + ", manaCost - 0", healthPotion.toString(),
            "It is not the expected level");
    }
}
