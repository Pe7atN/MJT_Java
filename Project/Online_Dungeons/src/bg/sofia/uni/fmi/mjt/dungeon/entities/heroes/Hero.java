package bg.sofia.uni.fmi.mjt.dungeon.entities.heroes;

import bg.sofia.uni.fmi.mjt.dungeon.entities.Entity;
import bg.sofia.uni.fmi.mjt.dungeon.entities.minions.Minion;
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
import bg.sofia.uni.fmi.mjt.dungeon.map.Dungeon;
import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;

import java.util.ArrayList;
import java.util.List;

public class Hero extends Entity implements HeroAPI {

    private int indexInDungeon;
    private List<Treasure> backPack;
    private Weapon weaponInHand;
    private Dungeon dungeon;
    private int currentMana;
    private int currentXP;
    private boolean isDead = false;

    private static final int XP_FOR_TREASURE = 1;
    private static final int MAX_CAPACITY_BACKPACK = 10;
    private static final int MIN_DODGE_CHANCE = 9;
    private static final int FULL_PERCENTAGE = 100;

    private static int playersCount = 0;

    public Hero(Dungeon dungeon, Location location) {
        super(location);
        this.dungeon = dungeon;
        this.indexInDungeon = ++playersCount;
        backPack = new ArrayList<>(MAX_CAPACITY_BACKPACK);
        currentMana = getStats().mana();
        weaponInHand = null;
        currentXP = 0;
    }

    public Hero(Dungeon dungeon, Location location, int level) {
        super(location, level);
        backPack = new ArrayList<>(MAX_CAPACITY_BACKPACK);
        this.dungeon = dungeon;
        this.indexInDungeon = ++playersCount;
        currentMana = getStats().mana();
        weaponInHand = null;
        currentXP = 0;

    }

    @Override
    public int getIndex() {
        return indexInDungeon;
    }

    @Override
    public int getCurrentMana() {
        return currentMana;
    }

    @Override
    public Weapon getWeaponInHand() {
        return weaponInHand;
    }

    @Override
    public List<Treasure> getBackPack() {
        return List.copyOf(backPack);
    }

    @Override
    public int getCurrentXP() {
        return currentXP;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void death() {
        setCurrentHealth(0);
        this.isDead = true;
        if (backPack.isEmpty()) {
            return;
        }

        int size = backPack.size();
        int randomNumber = (int) (Math.random() * size);
        backPack.remove(randomNumber);
    }

    @Override
    public void respawn() {
        this.isDead = false;
        currentMana = getStats().mana();
        setCurrentHealth(getStats().health());
        currentXP = 0;
    }

    @Override
    public int attack() throws DeadPlayerCouldNotActException, NoEnemyToAttackException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (!dungeon.isAttackable(getLocation())) {
            throw new NoEnemyToAttackException("This is not for attack");
        }

        if (dungeon.isMinion(getLocation())) {
            Minion minion = dungeon.getMinions().get(getLocation());
            return attackingMinion(minion);
        }

        Hero heroToAttack = dungeon.getAnotherHero(getLocation(), this);
        return attackingHero(heroToAttack);
    }

    @Override
    public int useSpell(int index)
        throws NotEnoughManaException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        NoEnemyToAttackException, CouldOnlyCastASpellException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (!dungeon.isAttackable(getLocation())) {
            throw new NoEnemyToAttackException("This is not for attack");
        }

        validateSpell(backPack.get(index));

        Spell spell = (Spell) backPack.get(index);
        if (dungeon.isMinion(getLocation())) {
            Minion minion = dungeon.getMinions().get(getLocation());
            minion.receiveDamage(spell.getAttack());
            endOfBattle(minion);
        }

        if (dungeon.isAnotherHero(getLocation())) {
            Hero hero = dungeon.getAnotherHero(getLocation(), this);
            hero.receiveDamage(spell.getAttack());
            endOfBattle(hero);
        }

        currentMana -= spell.getManaCost();
        return spell.getAttack();
    }

    @Override
    public boolean chanceOfDodge() {
        int dodgeChance = MIN_DODGE_CHANCE + getLevel();

        int randomNumber = (int) (Math.random() * FULL_PERCENTAGE) + 1;
        return randomNumber <= dodgeChance;
    }

    @Override
    public void useItem(int index)
        throws IllegalIndexForInventoryException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        CouldNotUseSpellOutOfBattleException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (index < 0) {
            throw new IllegalIndexForInventoryException("Cannot be negative");
        }

        if (index >= backPack.size()) {
            throw new IllegalIndexForInventoryException("Out of bounds of the backPack");
        }

        Treasure currentItem = backPack.get(index);

        if (getLevel() < currentItem.getLevel()) {
            throw new NotEnoughLevelException("You cannot use this item");
        }

        useCurrentItem(currentItem, index);
    }

    @Override
    public void giveItem(int index)
        throws IllegalIndexForInventoryException, DeadPlayerCouldNotActException, BackPackIsFullException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (index < 0) {
            throw new IllegalIndexForInventoryException("Cannot be negative");
        }

        if (index >= backPack.size()) {
            throw new IllegalIndexForInventoryException("Out of bounds of the backPack");
        }

        Hero heroToGive = dungeon.getAnotherHero(getLocation(), this);

        if (heroToGive == null) {
            return;
        }

        Treasure treasureToGive = backPack.get(index);

        //We gave the item in our hand
        if (treasureToGive.equals(weaponInHand)) {
            weaponInHand = null;
        }

        backPack.remove(index);
        heroToGive.receiveItem(treasureToGive);
    }

    @Override
    public void removeItem(int index) throws IllegalIndexForInventoryException, DeadPlayerCouldNotActException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (index < 0) {
            throw new IllegalIndexForInventoryException("Cannot be negative");
        }

        if (index >= backPack.size()) {
            throw new IllegalIndexForInventoryException("Out of bounds of the backPack");
        }

        //We removed the item which is our hand
        if (backPack.get(index).equals(weaponInHand)) {
            weaponInHand = null;
        }

        backPack.remove(index);
    }

    @Override
    public void receiveItem(Treasure item) throws DeadPlayerCouldNotActException, BackPackIsFullException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (backPack.size() == MAX_CAPACITY_BACKPACK) {
            throw new BackPackIsFullException("Backpack is full");
        }

        backPack.add(item);
    }

    @Override
    public void pickTreasure()
        throws DeadPlayerCouldNotActException, BackPackIsFullException, NoTreasureAtLocationException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        if (!dungeon.isTreasure(getLocation())) {
            throw new NoTreasureAtLocationException("Here there is no treasure");
        }

        Treasure currentItem = dungeon.getTreasures().get(getLocation());
        if (backPack.size() == MAX_CAPACITY_BACKPACK) {
            throw new BackPackIsFullException("BackPack is full");
        }

        receiveXP(XP_FOR_TREASURE);
        backPack.add(currentItem);
        dungeon.removeTreasureFromLocation(getLocation());
    }

    @Override
    public void receiveXP(int experience) {
        currentXP += experience;
        // Everytime when a hero receive an experience
        // we have to check if it can level up
        levelUpHero();
    }

    @Override
    public void moveUp() throws IllegalPositionException, DeadPlayerCouldNotActException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        Location locationToMove = new Location(getLocation().x() - 1, getLocation().y());
        if (!dungeon.isValidPosition(locationToMove)) {
            throw new IllegalPositionException("Invalid position in dungeon.");
        }

        dungeon.moveHero(this, getLocation(), locationToMove);
        changeLocation(locationToMove);
    }

    @Override
    public void moveDown() throws IllegalPositionException, DeadPlayerCouldNotActException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        Location locationToMove = new Location(getLocation().x() + 1, getLocation().y());
        if (!dungeon.isValidPosition(locationToMove)) {
            throw new IllegalPositionException("Invalid position in dungeon.");
        }

        dungeon.moveHero(this, getLocation(), locationToMove);
        changeLocation(locationToMove);
    }

    @Override
    public void moveRight() throws IllegalPositionException, DeadPlayerCouldNotActException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        Location locationToMove = new Location(getLocation().x(), getLocation().y() + 1);
        if (!dungeon.isValidPosition(locationToMove)) {
            throw new IllegalPositionException("Invalid position in dungeon.");
        }

        dungeon.moveHero(this, getLocation(), locationToMove);
        changeLocation(locationToMove);
    }

    @Override
    public void moveLeft() throws IllegalPositionException, DeadPlayerCouldNotActException {
        if (isDead()) {
            throw new DeadPlayerCouldNotActException("Hero cannot act when dead");
        }

        Location locationToMove = new Location(getLocation().x(), getLocation().y() - 1);
        if (!dungeon.isValidPosition(locationToMove)) {
            throw new IllegalPositionException("Invalid position in dungeon.");
        }

        dungeon.moveHero(this, getLocation(), locationToMove);
        changeLocation(locationToMove);
    }

    private void levelUpHero() {
        int neededXP = (int) Math.pow(2, this.getLevel());

        if (neededXP <= currentXP) {
            levelUp(); //this is a function from Entity class
            updateStats(); //this is a function from Entity class

            //After a level up, the health and the mana are recovered to full
            setCurrentHealth(getStats().health());
            currentMana = getStats().mana();
            currentXP -= neededXP;
        }
    }

    private void useCurrentItem(Treasure item, int index)
        throws CouldNotUseSpellOutOfBattleException, IllegalIndexForInventoryException, DeadPlayerCouldNotActException {
        if (item instanceof Weapon) {
            weaponInHand = (Weapon) item;
            return;
        }

        if (item instanceof ManaPotion) {
            currentMana += item.getAttack();
            currentMana = Math.min(currentMana, getStats().mana());
            removeItem(index);
            return;
        }

        if (item instanceof HealthPotion) {
            setCurrentHealth(getCurrentHealth() + item.getAttack());
            setCurrentHealth(Math.min(getCurrentHealth(), getStats().health()));
            removeItem(index);
            return;
        }

        if (item instanceof Spell) {
            throw new CouldNotUseSpellOutOfBattleException("Cannot use if not in battle!");
        }
    }

    private int attackingHero(Hero hero) {
        int damage = getStats().attack();

        if (weaponInHand != null) {
            damage += weaponInHand.getAttack();
        }

        hero.receiveDamage(damage);
        getHitByHero(hero);
        endOfBattle(hero);
        return damage;
    }

    private int attackingMinion(Minion minion) {
        int damage = getStats().attack();

        if (getWeaponInHand() != null) {
            damage += getWeaponInHand().getAttack();
        }

        minion.receiveDamage(damage);
        getHitByMinion(minion);
        endOfBattle(minion);
        return damage;
    }

    private void getHitByMinion(Minion minion) {
        if (minion.getCurrentHealth() > 0) {
            if (!chanceOfDodge()) {
                receiveDamage(minion.getStats().attack());
            }
        }
    }

    private void endOfBattle(Entity entity) {
        if (getCurrentHealth() <= 0) {
            death();
        } else if (entity.getCurrentHealth() <= 0) {
            if (entity instanceof Minion minion) {
                receiveXP(minion.droppedXp());
                dungeon.removeMinionFromLocation(getLocation());
            } else if (entity instanceof Hero hero) {
                receiveXP(hero.getLevel());
                hero.death();
            }
        }
    }

    private void getHitByHero(Hero hero) {
        if (hero.getCurrentHealth() > 0) {
            int damage = hero.getStats().attack();

            if (hero.getWeaponInHand() != null) {
                damage += hero.getWeaponInHand().getAttack();
            }

            if (!chanceOfDodge()) {
                receiveDamage(damage);
            }
        }
    }

    private void validateSpell(Treasure item)
        throws NotEnoughManaException, NotEnoughLevelException, CouldOnlyCastASpellException {
        if (!(item instanceof Spell spell)) {
            throw new CouldOnlyCastASpellException("It has to be a spell");
        }

        if (spell.getLevel() > getLevel()) {
            throw new NotEnoughLevelException("Not enough level to use!");
        }

        if (spell.getManaCost() > currentMana) {
            throw new NotEnoughManaException("Not enough mana to use!");
        }
    }

}
