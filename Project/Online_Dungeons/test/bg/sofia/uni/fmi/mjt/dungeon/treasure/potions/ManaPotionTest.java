package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManaPotionTest {
    ManaPotion manaPotion = new ManaPotion(new Location(0, 0), 2);
    ManaPotion manaPotionTest = new ManaPotion(new Location(1, 0));

    @Test
    void testToString() {
        int attack = manaPotion.getAttack();
        assertEquals("ManaPotion: level - 2, attack - " + attack + ", manaCost - 0", manaPotion.toString(),
            "It is not the expected level");
    }
}
