package bg.sofia.uni.fmi.mjt.dungeon.entities.heroes;

import bg.sofia.uni.fmi.mjt.dungeon.exception.BackPackIsFullException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.CouldNotUseSpellOutOfBattleException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.CouldOnlyCastASpellException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.DeadPlayerCouldNotActException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.IllegalIndexForInventoryException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.IllegalPositionException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NoEnemyToAttackException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NoTreasureAtLocationException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NotEnoughLevelException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.NotEnoughManaException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

import java.util.List;

public interface HeroAPI {

    /**
     * @return the index of the hero in the dungeon
     */
    int getIndex();

    /**
     * @return the current mana of the hero
     */
    int getCurrentMana();

    /**
     * @return the current weapon in the hand. It could be null
     * if there is no weapon in the hand of the hero
     */
    Weapon getWeaponInHand();

    /**
     * @return the current experience of the hero
     */
    int getCurrentXP();

    /**
     * @return the backpack of the hero as a list
     */
    List<Treasure> getBackPack();

    /**
     * @return if the hero is dead;
     */
    boolean isDead();

    /**
     * When a hero dies, he loses one random item from his backpack.
     */
    void death();

    /**
     * It respawns the hero.
     */
    void respawn();

    /**
     * @return if the hero can dodge based on his chance
     */
    boolean chanceOfDodge();

    /**
     * The hero uses a certain item in the backpack. If it's a weapon it equips it in his hand. If it's a potion
     * restores certain mana or health. Spells could only be used in battles.
     *
     * @param index the index of the item which is wanted to be used
     * @throws IllegalIndexForInventoryException    if index is less than zero or bigger than the size of the backpack
     * @throws IllegalIndexForInventoryException    if the hero tries to use spell when not in battle
     * @throws NotEnoughLevelException              if the level of the hero is less than the level of the item
     * @throws DeadPlayerCouldNotActException       if the player is dead
     * @throws CouldNotUseSpellOutOfBattleException if the hero tries to use spell when out of battle
     */
    void useItem(int index)
        throws IllegalIndexForInventoryException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        CouldNotUseSpellOutOfBattleException;

    /**
     * It gives an item from the backpack to another hero
     *
     * @param index the index of the item it is going to be given to another hero
     * @throws IllegalIndexForInventoryException if index is less than zero or bigger than the size of the backpack
     * @throws DeadPlayerCouldNotActException    if the player is dead
     * @throws BackPackIsFullException           if the other hero's backpack is full
     */
    void giveItem(int index)
        throws IllegalIndexForInventoryException, DeadPlayerCouldNotActException, BackPackIsFullException;

    /**
     * The hero receives an item from another hero
     *
     * @param item the new location of the entity
     * @throws IllegalArgumentException       if item is null or the backPack is full
     * @throws DeadPlayerCouldNotActException if the player is dead
     * @throws BackPackIsFullException        if the backpack of the hero is full
     */
    void receiveItem(Treasure item) throws DeadPlayerCouldNotActException, BackPackIsFullException;

    /**
     * It removes an item from the backpack at certain index in the backpack
     *
     * @param index the index of the item which is going to be removed from the backpack
     * @throws IllegalIndexForInventoryException if index is less than zero or bigger than the size of the backpack
     * @throws DeadPlayerCouldNotActException    if the player is dead
     */
    void removeItem(int index) throws IllegalIndexForInventoryException, DeadPlayerCouldNotActException;

    /**
     * It picks up the treasure if it is at the same location
     *
     * @throws IllegalArgumentException       if on the location there is no treasure
     * @throws DeadPlayerCouldNotActException if the player is dead
     * @throws BackPackIsFullException        if the backpack of the hero is full
     * @throws NoTreasureAtLocationException  if at the location there is no treasure
     */
    void pickTreasure() throws DeadPlayerCouldNotActException, BackPackIsFullException, NoTreasureAtLocationException;

    /**
     * Attacks a hero or a minion with a sword when you are at the same location as them
     *
     * @return the damage which the hero did
     * @throws DeadPlayerCouldNotActException if the player is dead
     * @throws NoEnemyToAttackException       if there is no minion or hero at the location
     */
    int attack() throws DeadPlayerCouldNotActException, NoEnemyToAttackException;

    /**
     * Attacks a hero or a minion with a spell when you are at the same location as them
     *
     * @return the damage which the hero did
     * @throws NotEnoughLevelException        if the player is trying to use a weapon with higher level
     * @throws NotEnoughManaException         if the player is trying to use a spell when not having enough mana
     * @throws DeadPlayerCouldNotActException if the player is dead
     * @throws NoEnemyToAttackException       if there is no minion or hero at the location
     * @throws CouldOnlyCastASpellException   if the hero tries to case something that is not spell
     */
    int useSpell(int index) throws NotEnoughManaException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        NoEnemyToAttackException, CouldOnlyCastASpellException;

    /**
     * It adds the given experience to the current experience of the hero
     *
     * @param experience the experience which is received
     */
    void receiveXP(int experience);

    /**
     * The hero goes up in the dungeon
     *
     * @throws IllegalPositionException       if the location next is invalid
     * @throws DeadPlayerCouldNotActException if the player is dead
     */
    void moveUp() throws IllegalPositionException, DeadPlayerCouldNotActException;

    /**
     * The hero goes down in the dungeon
     *
     * @throws IllegalPositionException       if the location next is invalid
     * @throws DeadPlayerCouldNotActException if the player is dead
     */
    void moveDown() throws IllegalPositionException, DeadPlayerCouldNotActException;

    /**
     * The hero goes right in the dungeon
     *
     * @throws IllegalPositionException       if the location next is invalid
     * @throws DeadPlayerCouldNotActException if the player is dead
     */
    void moveRight() throws IllegalPositionException, DeadPlayerCouldNotActException;

    /**
     * The hero goes left in the dungeon
     *
     * @throws IllegalPositionException       if the location next is invalid
     * @throws DeadPlayerCouldNotActException if the player is dead
     */
    void moveLeft() throws IllegalPositionException, DeadPlayerCouldNotActException;
}
