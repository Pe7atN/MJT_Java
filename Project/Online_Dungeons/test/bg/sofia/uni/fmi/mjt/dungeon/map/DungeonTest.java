package bg.sofia.uni.fmi.mjt.dungeon.map;

import bg.sofia.uni.fmi.mjt.dungeon.entities.heroes.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.entities.minions.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DungeonTest {
    char[][] dungeonMap = {
        {'#', '.', '#', '.', '.', '#', '.', '.', '#', '.'},
        {'.', '.', '#', '#', '.', '#', '.', '#', '#', '.'},
        {'.', '.', '#', '.', '.', '#', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '#', '#'}
    };

    Dungeon dungeon = new Dungeon(dungeonMap);

    @Test
    void testGetTreasures() {
        Map<Location, Treasure> treasures = dungeon.getTreasures();
        assertNotNull(treasures);
        assertEquals(5, treasures.size(),
            "It is expected that there are always 5 treasures");
    }

    @Test
    void testGetMinions() {
        Map<Location, Minion> minions = dungeon.getMinions();
        assertNotNull(minions);
        assertEquals(5, minions.size(),
            "It is expected that there are always 5 minions");
    }

    @Test
    void testIsMinion() {
        assertFalse(dungeon.isMinion(new Location(0, 0)));
    }

    @Test
    void testIsMinionLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isMinion(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsHero() {
        assertFalse(dungeon.isHero(new Location(0, 0)));
    }

    @Test
    void testIsHeroLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isHero(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsAnotherHero() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 0));
        dungeon.addHero(hero);
        Location loc = hero.getLocation();
        assertFalse(dungeon.isAnotherHero(loc),
            "It was expected that there is no another hero");
    }

    @Test
    void testIsAnotherHeroLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isAnotherHero(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsAttackable() {
        Set<Location> locations = dungeon.getMinions().keySet();
        for (Location loc : locations) {
            assertTrue(dungeon.isAttackable(loc),
                "It was expected that every location with a minion is attackable");
        }
    }

    @Test
    void testIsAttackableLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isAttackable(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsTreasure() {
        Set<Location> locations = dungeon.getTreasures().keySet();
        for (Location loc : locations) {
            assertTrue(dungeon.isTreasure(loc),
                "It was expected that every location has a treasure");
        }
        assertFalse(dungeon.isTreasure(new Location(0, 0)));
    }

    @Test
    void testIsTreasureLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isTreasure(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsWall() {
        assertTrue(dungeon.isWall(new Location(0, 0)));
    }

    @Test
    void testIsWallLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isWall(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsValidPosition() {
        assertTrue(dungeon.isValidPosition(new Location(1, 1)));
        assertFalse(dungeon.isValidPosition(new Location(-1, 0)));
    }

    @Test
    void testIsValidPositionLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isValidPosition(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testIsFreeSpace() {
        dungeon.freeCertainSpace(new Location(1, 1));
        assertTrue(dungeon.isFreeSpace(new Location(1, 1)));
        assertFalse(dungeon.isFreeSpace(new Location(0, 0)));
    }

    @Test
    void testIsFreeSpaceLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.isFreeSpace(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testFreeCertainSpace() {
        Location location = new Location(1, 1);
        dungeon.freeCertainSpace(location);
        assertTrue(dungeon.isFreeSpace(location));
    }

    @Test
    void testFreeCertainSpaceLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.freeCertainSpace(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testAddHero() {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        assertDoesNotThrow(() -> dungeon.addHero(hero));
    }

    @Test
    void testAddHeroMultipleVariants() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        Hero hero1 = new Hero(dungeon, new Location(1, 1));
        Hero hero2 = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero1);
        dungeon.addHero(hero2);
        assertDoesNotThrow(() -> dungeon.addHero(hero));
    }

    @Test
    void testAddHeroWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.addHero(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testRemoveHero() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        dungeon.removeHero(hero);
        assertFalse(dungeon.isHero(new Location(1, 1)));
    }

    @Test
    void testRemoveHeroWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.removeHero(null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testPlaceNewMinion() {
        assertDoesNotThrow(dungeon::placeNewMinion);
    }

    @Test
    void testPlaceNewTreasure() {
        assertDoesNotThrow(dungeon::placeNewTreasure);
    }

    @Test
    void testMoveHero() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        Location from = hero.getLocation();
        Location to = new Location(0, 1);
        dungeon.moveHero(hero, from, to);
        assertFalse(dungeon.isHero(from));
        assertTrue(dungeon.isHero(to));
    }

    @Test
    void testMoveHeroWhenFromIsNull() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        assertThrows(IllegalArgumentException.class,
            () -> dungeon.moveHero(hero, null, new Location(0, 1)),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testMoveHeroWhenToIsNull() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        assertThrows(IllegalArgumentException.class,
            () -> dungeon.moveHero(hero, new Location(0, 0), null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testMoveHeroWhenHeroIsNull() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> dungeon.moveHero(null, new Location(0, 0), new Location(0, 1)),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testGetAnotherHeroWhenHeroIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dungeon.getAnotherHero(new Location(0, 0), null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testGetAnotherHeroWhenLocationIsNull() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        assertThrows(IllegalArgumentException.class, () -> dungeon.getAnotherHero(null, null),
            "It was expected IllegalArgumentException to be thrown");
    }

    @Test
    void testGetAnotherHeroWhenHeroIsAlone() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        Location loc = hero.getLocation();
        assertNull(dungeon.getAnotherHero(loc, hero), "It was expected to return null");
    }

    @Test
    void testGetAnotherHero() throws Exception {
        Hero hero = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(hero);
        Location loc = hero.getLocation();
        Hero heroSnd = new Hero(dungeon, new Location(1, 1));
        dungeon.addHero(heroSnd);
        Location locSnd = heroSnd.getLocation();
        dungeon.moveHero(hero, loc, locSnd);
        assertEquals(hero, dungeon.getAnotherHero(locSnd, heroSnd),
            "It was not the expected hero");
    }

}

