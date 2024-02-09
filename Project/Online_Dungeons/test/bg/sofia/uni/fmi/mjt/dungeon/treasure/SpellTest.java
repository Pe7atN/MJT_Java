package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpellTest {
    Spell spell = new Spell(new Location(0, 0), 2);
    Spell spellFirstLevel = new Spell(new Location(1, 0));

    @Test
    void testGetManaCost() {
        assertTrue(spell.getManaCost() >= 10, "It was expected to be between 10 and 12");
        assertTrue(spell.getManaCost() <= 12, "It was expected to be between 10 and 12");
    }

    @Test
    void testToString() {
        int attack = spell.getAttack();
        int mana = spell.getManaCost();
        assertEquals("Spell: level - 2, attack - " + attack + ", manaCost - " + mana, spell.toString(),
            "It is not the expected level");
    }
}
