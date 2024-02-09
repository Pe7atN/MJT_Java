package bg.sofia.uni.fmi.mjt.dungeon.entities.heroes;

import bg.sofia.uni.fmi.mjt.dungeon.entities.minions.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.exception.CouldNotUseSpellOutOfBattleException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.DeadPlayerCouldNotActException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.IllegalIndexForInventoryException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.IllegalPositionException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NoEnemyToAttackException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NoTreasureAtLocationException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NotEnoughLevelException;
import bg.sofia.uni.fmi.mjt.dungeon.map.Dungeon;
import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeroTest {
    char[][] dungeonMap = {
        {'#', '.', '#', '.', '.', '#', '.', '.', '#', '.'},
        {'.', '.', '#', '#', '.', '#', '.', '#', '#', '.'},
        {'.', '.', '#', '.', '.', '#', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '#', '#'}
    };

    Dungeon dungeon = new Dungeon(dungeonMap);
    Hero testHero = new Hero(dungeon, new Location(1, 1));

    @Test
    void testGetLocation() {
        assertEquals(new Location(1, 1), testHero.getLocation(),
            "It is not the expected location");
    }

    @Test
    void testGetWeaponInHand() {
        assertNull(testHero.getWeaponInHand(), "It is not the expected weapon in hand");
    }

    @Test
    void testGetBackPack() {
        assertEquals(List.of(), testHero.getBackPack(),
            "It is not the expected index");
    }

    @Test
    void testGetCurrentXP() {
        testHero.receiveXP(1);
        assertEquals(1, testHero.getCurrentXP(),
            "It is not the expected experience");
    }

    @Test
    void testGetLevel() {
        testHero.receiveXP(2);
        assertEquals(2, testHero.getLevel(),
            "It is not the expected level");
    }

    @Test
    void testisDead() {
        testHero.death();
        assertTrue(testHero.isDead(),
            "It is expected that the hero is dead");

        assertEquals(0, testHero.getCurrentHealth(),
            "It is expected that the hero is dead");
    }

    @Test
    void testisNotDead() {
        assertFalse(testHero.isDead(),
            "It is expected that the hero is not dead");
    }

    @Test
    void testDeathRemovingRandomItemFromBackPack() throws Exception {
        testHero.receiveItem(new Weapon(new Location(0, 0), 2));
        testHero.death();
        assertTrue(testHero.getBackPack().isEmpty(),
            "It is expected that the backpack is empty");
    }

    @Test
    void testRespawnHero() {
        testHero.death();
        testHero.respawn();
        assertEquals(testHero.getStats().health(), testHero.getCurrentHealth(),
            "It is expected that the hero is respawned");

        assertEquals(testHero.getStats().mana(), testHero.getCurrentMana(),
            "It is expected that the hero is respawned");

        assertEquals(0, testHero.getCurrentXP(),
            "It is expected that the hero is respawned");

        assertFalse(testHero.isDead(),
            "It is expected that the hero is respawned");
    }

    @Test
    void testAttackWhenDead() {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.attack(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testAttackWhenThereIsNoAttackableAtLocation() throws Exception {
        dungeon.addHero(testHero);
        assertThrows(NoEnemyToAttackException.class, () -> testHero.attack(),
            "It is expected NoEnemyToAttackException to be thrown ");
    }

    @Test
    void testAttackWhenAttackingHero() throws Exception {
        Hero hero1 = new Hero(dungeon, new Location(0, 1), 2);
        Hero hero2 = new Hero(dungeon, new Location(0, 1), 3);
        dungeon.addHero(hero1);
        dungeon.addHero(hero2);
        int damage1 = hero1.getStats().attack();
        int damage2 = hero2.getStats().attack();
        dungeon.moveHero(hero2, hero2.getLocation(), hero1.getLocation());
        hero1.attack();
        //If he dodges the attack
        if (hero1.getCurrentHealth() == hero1.getStats().health()) {
            hero1.receiveDamage(damage2);
        }

        assertEquals(hero1.getStats().health() - damage2, hero1.getCurrentHealth(),
            "It is expected that the hero received damage");

        assertEquals(hero2.getStats().health() - damage1, hero2.getCurrentHealth(),
            "It is expected that the hero received damage");
    }

    @Test
    void testAttackWhenAttackingMinion() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);
        Location location = null;
        for (Location loc : dungeon.getMinions().keySet()) {
            location = loc;
        }

        Minion minion = dungeon.getMinions().get(location);
        dungeon.moveHero(hero, hero.getLocation(), minion.getLocation());
        hero.attack();
        int damageHero = hero.getStats().attack();
        int damageMinion = minion.getStats().attack();
        //If he dodges the attack
        if (hero.getCurrentHealth() == hero.getStats().health()) {
            hero.receiveDamage(damageMinion);
        }

        assertEquals(hero.getStats().health() - damageMinion, hero.getCurrentHealth(),
            "It is expected that the hero received damage");

        assertEquals(minion.getStats().health() - damageHero, minion.getCurrentHealth(),
            "It is expected that the minion received damage");
    }

    @Test
    void testAttackWhenKillingMinion() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);
        Location location = null;
        for (Location loc : dungeon.getMinions().keySet()) {
            location = loc;
        }

        Minion minion = dungeon.getMinions().get(location);
        dungeon.moveHero(hero, hero.getLocation(), minion.getLocation());
        hero.attack();
        hero.attack();

        assertTrue(hero.getCurrentXP() >= 1, "It was expected to receive experience");
        assertTrue(hero.getCurrentXP() <= 2, "It was expected to receive experience");
    }

    @Test
    void testAttackWhenDying() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);
        hero.receiveDamage(60);
        Location location = null;
        for (Location loc : dungeon.getMinions().keySet()) {
            location = loc;
        }

        Minion minion = dungeon.getMinions().get(location);
        dungeon.moveHero(hero, hero.getLocation(), minion.getLocation());
        hero.attack();
        while (hero.getCurrentHealth() == 50) {
            hero.attack();
        }

        assertTrue(hero.isDead(),
            "It is expected the hero to be dead");
    }

    @Test
    void testReceiveXP() {
        testHero.receiveXP(1);
        assertEquals(1, testHero.getCurrentXP(),
            "It was not the expected amount of experience");
    }

    @Test
    void testReceiveXPAndLevelUp() {
        testHero.receiveXP(2);
        assertEquals(0, testHero.getCurrentXP(),
            "It was not the expected amount of experience");
        assertEquals(2, testHero.getLevel(),
            "It was the expected the hero to level up");
    }

    @Test
    void testMoveUp() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(3, 1));
        testHero.moveUp();
        assertEquals(new Location(2, 1), testHero.getLocation(),
            "The hero is not on the expected location");

    }

    @Test
    void testMoveDown() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(2, 1));
        testHero.moveDown();
        assertEquals(new Location(3, 1), testHero.getLocation(),
            "The hero is not on the expected location");

    }

    @Test
    void testMoveLeft() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(3, 1));
        testHero.moveLeft();
        assertEquals(new Location(3, 0), testHero.getLocation(),
            "The hero is not on the expected location");

    }

    @Test
    void testMoveRight() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(3, 1));
        testHero.moveRight();
        assertEquals(new Location(3, 2), testHero.getLocation(),
            "The hero is not on the expected location");

    }

    @Test
    void testMoveUpWhenDead() throws Exception {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.moveUp(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testMoveDownWhenDead() throws Exception {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.moveDown(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testMoveRightWhenDead() throws Exception {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.moveRight(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testMoveLeftWhenDead() throws Exception {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.moveLeft(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testMoveUpInvalidPosition() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(0, 1));
        assertThrows(IllegalPositionException.class, () -> testHero.moveUp(),
            "It is expected IllegalPositionException to be thrown ");

    }

    @Test
    void testMoveDownInvalidPosition() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(3, 1));
        assertThrows(IllegalPositionException.class, () -> testHero.moveDown(),
            "It is expected IllegalPositionException to be thrown ");

    }

    @Test
    void testMoveLeftInvalidPosition() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(0, 1));
        assertThrows(IllegalPositionException.class, () -> testHero.moveLeft(),
            "It is expected IllegalPositionException to be thrown ");

    }

    @Test
    void testMoveRightInvalidPosition() throws Exception {
        dungeon.addHero(testHero);
        dungeon.moveHero(testHero, testHero.getLocation(), new Location(0, 1));
        assertThrows(IllegalPositionException.class, () -> testHero.moveRight(),
            "It is expected IllegalPositionException to be thrown ");

    }

    @Test
    void testPickTreasure() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);
        Location location = null;
        for (Location loc : dungeon.getTreasures().keySet()) {
            location = loc;
        }

        Treasure treasure = dungeon.getTreasures().get(location);
        dungeon.moveHero(hero, hero.getLocation(), treasure.getLocation());
        hero.pickTreasure();
        assertEquals(1, hero.getBackPack().size(),
            "It was expected that there is an item in the backpack");
        assertEquals(1, hero.getCurrentXP(),
            "It was expected that the hero will receive experience");
    }

    @Test
    void testPickTreasureWhenDead() throws Exception {
        testHero.death();

        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.pickTreasure(),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testPickTreasureWhenThereIsNoTreasure() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);

        assertThrows(NoTreasureAtLocationException.class, () -> hero.pickTreasure(),
            "It is expected NoTreasureAtLocationException to be thrown ");
    }

    @Test
    void testReceiveItemWhenDead() {
        Treasure treasure = new Treasure(new Location(0, 1));
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.receiveItem(treasure),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testReceiveItemWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> testHero.receiveItem(null),
            "It is expected IllegalArgumentException to be thrown ");
    }

    @Test
    void testRemoveItemWhenDead() {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.removeItem(2),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testRemoveItemWhenInvalidIndex() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.removeItem(-1),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }

    @Test
    void testRemoveItemWhenInvalidIndexBiggerThanSize() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.removeItem(11),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }

    @Test
    void testRemoveTreasure() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero);
        Location location = null;
        for (Location loc : dungeon.getTreasures().keySet()) {
            location = loc;
        }

        dungeon.moveHero(hero, hero.getLocation(), location);
        hero.pickTreasure();
        assertEquals(1, hero.getBackPack().size(),
            "It was expected that there is an item in the backpack");
        hero.removeItem(0);

        assertEquals(0, hero.getBackPack().size(),
            "It was expected that the hero has an empty backpack");
    }

    @Test
    void testGiveItemWhenDead() {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.giveItem(2),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testGiveItemWhenInvalidIndex() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.giveItem(-1),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }

    @Test
    void testGiveItemWhenInvalidIndexBiggerThanSize() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.giveItem(11),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }


    @Test
    void testGiveItemToAnotherHero() throws Exception {
        Hero hero1 = new Hero(dungeon, new Location(0, 1), 2);
        Hero hero2 = new Hero(dungeon, new Location(0, 1), 2);
        dungeon.addHero(hero1);
        dungeon.addHero(hero2);
        Location location = null;
        for (Location loc : dungeon.getTreasures().keySet()) {
            location = loc;
        }

        dungeon.moveHero(hero1, hero1.getLocation(), location);
        dungeon.moveHero(hero2, hero2.getLocation(), location);
        hero1.pickTreasure();
        assertEquals(1, hero1.getBackPack().size(),
            "It was expected that there is an item in the backpack");
        assertEquals(0, hero2.getBackPack().size(),
            "It was expected that the hero has an empty backpack");

        hero1.giveItem(0);
        assertEquals(0, hero1.getBackPack().size(),
            "It was expected that the hero has an empty backpack");
        assertEquals(1, hero2.getBackPack().size(),
            "It was expected that the hero has an empty backpack");
    }

    @Test
    void testUseItemWhenDead() {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.useItem(2),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testUseItemWhenInvalidIndex() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.useItem(-1),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }

    @Test
    void testUseItemWhenInvalidIndexBiggerThanSize() {
        assertThrows(IllegalIndexForInventoryException.class, () -> testHero.useItem(11),
            "It is expected IllegalIndexForInventoryException to be thrown ");
    }

    @Test
    void testUseItemWithHigherLevel() throws Exception {
        testHero.receiveItem(new Weapon(new Location(0,1),10));
        assertThrows(NotEnoughLevelException.class, () -> testHero.useItem(0),
            "It is expected NotEnoughLevelException to be thrown ");
    }

    @Test
    void testUseHealthPotion() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 11);
        dungeon.addHero(hero);
        hero.receiveDamage(20);
        int pastHealth = hero.getCurrentHealth();
        hero.receiveItem(new HealthPotion(new Location(0, 0)));
        hero.useItem(0);
        assertTrue(pastHealth < hero.getCurrentHealth(),
            "It is expected the hero to receive some health back");
        assertTrue(hero.getStats().health() >= hero.getCurrentHealth(),
            "It is not expected the hero to have more health than maxHealth");
    }

    @Test
    void testUseManaPotion() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 11);
        Hero hero1 = new Hero(dungeon, new Location(0, 1), 11);
        dungeon.addHero(hero);
        dungeon.addHero(hero1);
        dungeon.moveHero(hero, hero.getLocation(), hero1.getLocation());
        hero.receiveItem(new Spell(new Location(0, 1), 2));
        hero.receiveItem(new ManaPotion(new Location(0, 0)));
        hero.useSpell(0);
        int pastMana = hero.getCurrentMana();
        hero.useItem(1);
        assertTrue(pastMana < hero.getCurrentMana(),
            "It is expected the hero to receive some health back");
        assertTrue(hero.getStats().mana() >= hero.getCurrentMana(),
            "It is not expected the hero to have more health than maxHealth");

    }

    @Test
    void testUseSpellWhenNotInBattle() throws Exception {
        Hero hero = new Hero(dungeon, new Location(0, 1), 11);
        dungeon.addHero(hero);
        hero.receiveItem(new Spell(new Location(0, 0)));

        assertThrows(CouldNotUseSpellOutOfBattleException.class, () -> hero.useItem(0),
            "It is  expected CouldNotUseSpellOutOfBattleException to be thrown");
    }

    @Test
    void testUseSpellWhenDead() {
        testHero.death();
        assertThrows(DeadPlayerCouldNotActException.class, () -> testHero.useSpell(0),
            "It is expected DeadPlayerCouldNotActException to be thrown ");
    }

    @Test
    void testUseSpellWhenThereIsNoOneToAttack() throws Exception {
        dungeon.addHero(testHero);
        assertThrows(NoEnemyToAttackException.class, () -> testHero.useSpell(0),
            "It is expected NoEnemyToAttackException to be thrown ");
    }

    @Test
    void testUseSpellOnMinion() throws Exception {
        dungeon.addHero(testHero);
        Location location = null;
        for (Location loc : dungeon.getMinions().keySet()) {
            location = loc;
        }

        Minion minion = dungeon.getMinions().get(location);
        dungeon.moveHero(testHero, testHero.getLocation(), minion.getLocation());
        testHero.receiveItem(new Spell(new Location(0, 0), 1));
        testHero.useSpell(0);
        int damage = testHero.getBackPack().get(0).getAttack();

        assertEquals(testHero.getStats().health(), testHero.getCurrentHealth(),
            "It is expected that the hero did not received damage");

        assertEquals(minion.getStats().health() - damage, minion.getCurrentHealth(),
            "It is expected that the minion received damage");

        assertTrue(testHero.getStats().mana() > testHero.getCurrentMana(),
            "It is expected that the hero used mana for the spell");
    }

    @Test
    void testUseSpellOnHero() throws Exception {
        Hero heroToAttack = new Hero(dungeon, new Location(0, 0), 1);
        dungeon.addHero(testHero);
        dungeon.addHero(heroToAttack);

        dungeon.moveHero(testHero, testHero.getLocation(), heroToAttack.getLocation());
        testHero.receiveItem(new Spell(new Location(0, 0), 1));
        testHero.useSpell(0);
        int damage = testHero.getBackPack().get(0).getAttack();

        assertEquals(testHero.getStats().health(), testHero.getCurrentHealth(),
            "It is expected that the hero did not received damage");

        assertEquals(heroToAttack.getStats().health() - damage, heroToAttack.getCurrentHealth(),
            "It is expected that the minion received damage");

        assertTrue(testHero.getStats().mana() > testHero.getCurrentMana(),
            "It is expected that the hero used mana for the spell");
    }


}
