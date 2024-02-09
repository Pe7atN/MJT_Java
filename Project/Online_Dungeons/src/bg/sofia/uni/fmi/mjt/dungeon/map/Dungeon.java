package bg.sofia.uni.fmi.mjt.dungeon.map;

import bg.sofia.uni.fmi.mjt.dungeon.entities.heroes.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.entities.minions.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.exception.PlayerAlreadyInServerException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dungeon implements DungeonAPI {

    private static final int ROW_COUNT = 4;
    private static final int COLUMN_COUNT = 10;
    private char[][] dungeon;
    private Map<Location, List<Hero>> heroes;
    private Map<Location, Minion> minions;
    private Map<Location, Treasure> treasures;

    private static final int MAX_CAP_MINIONS_AND_TREASURES = 5;
    private static final int MAX_CAP_HEROES = 9;
    private static final int PART_OF_PERCANTAGE = 10;
    private static final int MAX_LEVEL = 10;

    public Dungeon(char[][] dungeon) {
        this.dungeon = dungeon;
        heroes = new HashMap<>(MAX_CAP_HEROES);
        minions = new HashMap<>(MAX_CAP_MINIONS_AND_TREASURES);
        treasures = new HashMap<>(MAX_CAP_MINIONS_AND_TREASURES);

        // putting minions and treasures on random places
        for (int i = 0; i < MAX_CAP_MINIONS_AND_TREASURES; i++) {
            placeNewMinion();
            placeNewTreasure();
        }
    }

    @Override
    public void printDungeon() {
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                System.out.print(dungeon[i][j] + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }

    @Override
    public Map<Location, Treasure> getTreasures() {
        return treasures;
    }

    @Override
    public Map<Location, Minion> getMinions() {
        return minions;
    }

    @Override
    public boolean isMinion(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return minions.containsKey(location);
    }

    @Override
    public boolean isHero(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return Character.isDigit(dungeon[location.x()][location.y()]);
    }

    @Override
    public boolean isAnotherHero(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return heroes.get(location).size() >= 2;
    }

    @Override
    public boolean isAttackable(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return isMinion(location) || isAnotherHero(location);
    }

    @Override
    public boolean isTreasure(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return treasures.containsKey(location);
    }

    @Override
    public boolean isWall(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        return dungeon[location.x()][location.y()] == '#';
    }

    @Override
    public boolean isValidPosition(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return location.x() < ROW_COUNT && location.y() < COLUMN_COUNT &&
            location.x() >= 0 && location.y() >= 0 && !isWall(location);
    }

    @Override
    public boolean isFreeSpace(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        return dungeon[location.x()][location.y()] == '.';
    }

    @Override
    public void freeCertainSpace(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        dungeon[location.x()][location.y()] = '.';
    }

    @Override
    public Hero getAnotherHero(Location location, Hero hero) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        if (!isAnotherHero(location)) {
            return null;
        }

        for (Hero currHero : heroes.get(location)) {
            if (!currHero.equals(hero)) {
                return currHero;
            }
        }

        return null;
    }

    @Override
    public void addHero(Hero hero) throws PlayerAlreadyInServerException {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        for (Location loc : heroes.keySet()) {
            for (Hero currHero : heroes.get(loc)) {
                if (currHero.getIndex() == hero.getIndex()) {
                    throw new PlayerAlreadyInServerException("Player is already in server");
                }
            }
        }

        Location location = pickRandomLocation(findAllFreeSpaces());
        dungeon[location.x()][location.y()] = (char) (hero.getIndex() + '0');

        if (!heroes.containsKey(location)) {
            heroes.put(location, new ArrayList<>());
            heroes.get(location).add(hero);
        }

        hero.changeLocation(location);
    }

    @Override
    public void removeHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        Location location = hero.getLocation();
        heroes.get(location).remove(hero);

        if (heroes.get(location).isEmpty()) {
            heroes.remove(location);
        }

        freeCertainSpace(location);
    }

    @Override
    public void moveHero(Hero hero, Location from, Location to) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        heroes.get(from).remove(hero);

        if (heroes.get(from).isEmpty()) {
            heroes.remove(from);
        }

        //If on the certain location there are not any heroes
        if (!heroes.containsKey(to)) {
            heroes.put(to, new ArrayList<>());
        }

        heroes.get(to).add(hero);
        dungeon[to.x()][to.y()] = (char) (hero.getIndex() + '0');
        hero.changeLocation(to);
        restoreLocation(from);
    }

    @Override
    public void placeNewMinion() {
        int averageLevel = findAverageLevel();
        Location location = pickRandomLocation(findAllFreeSpaces());

        dungeon[location.x()][location.y()] = 'M';
        minions.put(location, new Minion(location, averageLevel));
    }

    @Override
    public void placeNewTreasure() {
        int randomLevel = (int) (Math.random() * MAX_LEVEL + 1);
        Location location = pickRandomLocation(findAllFreeSpaces());
        dungeon[location.x()][location.y()] = 'T';

        int randomNumber = (int) (Math.random() * PART_OF_PERCANTAGE);
        final int spellChance = 2;
        final int spellChance1 = 3;
        final int spellChance2 = 4;
        switch (randomNumber) {
            case 0 -> treasures.put(location, new ManaPotion(location, randomLevel));
            case 1 -> treasures.put(location, new HealthPotion(location, randomLevel));
            case spellChance, spellChance1, spellChance2 -> treasures.put(location, new Spell(location, randomLevel));
            default -> treasures.put(location, new Weapon(location, randomLevel));
        }
    }

    @Override
    public void removeMinionFromLocation(Location location) {
        minions.remove(location);
        placeNewMinion();
    }

    @Override
    public void removeTreasureFromLocation(Location location) {
        treasures.remove(location);
        placeNewTreasure();
    }

    public void sendMapInformation(Selector selector) throws IOException {
        if (selector == null) {
            throw new IllegalArgumentException("Selector cannot be null");
        }

        Set<SelectionKey> keys = selector.keys();
        ByteBuffer mapInformation = mapToByteBuffer();

        for (SelectionKey key : keys) {
            if (key.isValid() && key.channel() instanceof SocketChannel channel) {
                ByteBuffer dataToSend = ByteBuffer.allocate(mapInformation.capacity());

                dataToSend.put(mapInformation.duplicate());
                dataToSend.flip();
                channel.write(dataToSend);
            }
        }
    }

    private ByteBuffer mapToByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Character.BYTES * (ROW_COUNT * COLUMN_COUNT + 2));
        byteBuffer.putChar((char) ROW_COUNT);
        byteBuffer.putChar((char) COLUMN_COUNT);

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                byteBuffer.putChar(dungeon[i][j]);
            }
        }

        byteBuffer.flip();
        return byteBuffer;
    }

    private int findAverageLevel() {
        if (heroes.isEmpty()) {
            return 1;
        }

        int sumOfLevels = 0;
        for (List<Hero> heroList : heroes.values()) {
            for (Hero hero : heroList) {
                sumOfLevels += hero.getLevel();
            }
        }

        return sumOfLevels / heroes.size();
    }

    private List<Location> findAllFreeSpaces() {
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                Location currentLocation = new Location(i, j);
                if (isFreeSpace(currentLocation)) {
                    locations.add(currentLocation);
                }
            }
        }

        return locations;
    }

    private Location pickRandomLocation(List<Location> locations) {
        int size = locations.size();
        int randomNumber = (int) (Math.random() * size);

        return locations.get(randomNumber);
    }

    private void restoreLocation(Location from) {
        if (minions.containsKey(from)) {
            dungeon[from.x()][from.y()] = 'M';
        } else if (treasures.containsKey(from)) {
            dungeon[from.x()][from.y()] = 'T';
        } else if (heroes.containsKey(from) && !heroes.get(from).isEmpty()) {
            Hero heroToPlace = heroes.get(from).getFirst();
            dungeon[from.x()][from.y()] = (char) (heroToPlace.getIndex() + '0');
        } else {
            freeCertainSpace(from);
        }
    }
}
