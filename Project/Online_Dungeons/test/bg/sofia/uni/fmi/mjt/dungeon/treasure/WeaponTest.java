package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeaponTest {
    Weapon weapon = new Weapon(new Location(0,0), 2);
    Weapon weaponFirstLevel = new Weapon(new Location(1,0));

    @Test
    void testToString(){
        int attack = weapon.getAttack();
        assertEquals("Weapon: level - 2, attack - " + attack, weapon.toString(),
            "It is not the expected level");
    }
}
