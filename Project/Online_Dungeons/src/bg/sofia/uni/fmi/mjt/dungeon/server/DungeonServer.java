package bg.sofia.uni.fmi.mjt.dungeon.server;

import bg.sofia.uni.fmi.mjt.dungeon.entities.heroes.Hero;
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
import bg.sofia.uni.fmi.mjt.dungeon.exception.PlayerAlreadyInServerException;
import bg.sofia.uni.fmi.mjt.dungeon.map.Dungeon;
import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class DungeonServer {
    private static final String EXCEPTIONS_LOG = "Exception_Log_Server.txt";

    private static final int BUFFER_SIZE = 4096;
    private static final int SERVER_PORT = 6666;
    private static final String HOST = "localhost";

    private boolean isServerWorking;

    private Dungeon dungeon;

    private ByteBuffer buffer;
    private Selector selector;

    public static void main(String[] args) {
        DungeonServer dungeonServer = new DungeonServer();
        dungeonServer.start();
    }

    private void start() {
        makeDungeon();
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    iterateThroughKeys();

                } catch (IOException e) {
                    logException(e);
                    throw new UncheckedIOException(
                        "Error occurred while processing client request:" + "Exception Logs saved to file: " +
                            Paths.get(EXCEPTIONS_LOG).toAbsolutePath(), e);
                }
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException(
                "Error occurred while trying to start the server:" + "Exception Logs saved to file: " +
                    Paths.get(EXCEPTIONS_LOG).toAbsolutePath(), e);
        }
    }

    private void iterateThroughKeys() throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            try {
                if (key.isReadable()) {
                    getClientInput(key);
                } else if (key.isAcceptable()) {
                    acceptNewClient(key);
                }
                dungeon.sendMapInformation(selector);
                keyIterator.remove();
            } catch (IllegalPositionException |
                     IllegalIndexForInventoryException | NotEnoughLevelException | DeadPlayerCouldNotActException |
                     NotEnoughManaException | BackPackIsFullException | CouldNotUseSpellOutOfBattleException |
                     NoTreasureAtLocationException | NoEnemyToAttackException | CouldOnlyCastASpellException e) {
                logException(e);
                handleException(key, e);
                dungeon.sendMapInformation(selector);
                keyIterator.remove();
            } catch (PlayerAlreadyInServerException e) {
                logException(e);
                dungeon.sendMapInformation(selector);
                keyIterator.remove();
            }
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, SERVER_PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void acceptNewClient(SelectionKey key) throws IOException, PlayerAlreadyInServerException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        Hero newPlayer = new Hero(dungeon, new Location(0, 0));
        accept.register(selector, SelectionKey.OP_READ, newPlayer);
        dungeon.addHero(newPlayer);
    }

    private void getClientInput(SelectionKey key) throws IOException, IllegalPositionException,
        IllegalIndexForInventoryException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        NotEnoughManaException, BackPackIsFullException, CouldNotUseSpellOutOfBattleException,
        NoTreasureAtLocationException, NoEnemyToAttackException, CouldOnlyCastASpellException {

        try {
            String command = getCommand(key);
            Hero hero = (Hero) key.attachment();
            String message = executeCommand(hero, command);
            if (message != null) {
                sendMessageToClient(key, message);
            }
        } catch (IOException e) {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("Client disconnected: " + channel.getRemoteAddress());
            key.cancel();
            channel.close();
            dungeon.removeHero((Hero) key.attachment());
        }
    }

    private String getCommand(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        buffer.clear();
        int r = channel.read(buffer);
        if (r < 0) {
            System.out.println("Client has closed the connection");
            channel.close();
            dungeon.removeHero((Hero) key.attachment());
            dungeon.sendMapInformation(selector);
            return "";
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void sendMessageToClient(SelectionKey key, String message) throws IOException {

        if (key.isValid() && key.channel() instanceof SocketChannel channel) {
            ByteBuffer dataToSend = ByteBuffer.allocate(message.length());
            dataToSend.put(message.getBytes());
            dataToSend.flip();
            channel.write(dataToSend);
        }
    }

    private void makeDungeon() {
        char[][] dungeon = {
            {'#', '.', '#', '.', '.', '#', '.', '.', '#', '.'},
            {'.', '.', '#', '#', '.', '#', '.', '#', '#', '.'},
            {'.', '.', '#', '.', '.', '#', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '#', '#'}
        };

        this.dungeon = new Dungeon(dungeon);
    }

    private static void logException(Exception exception) {
        try (PrintWriter writer = new PrintWriter(
            new BufferedWriter(new FileWriter(EXCEPTIONS_LOG, true)))) {
            writer.println(LocalDateTime.now());
            writer.println("Exception was thrown: " + exception.getMessage());
            exception.printStackTrace(writer);
        } catch (IOException e) {
            throw new UncheckedIOException(
                "Could not write logs to:" + Paths.get(EXCEPTIONS_LOG).toAbsolutePath(), e);
        }
    }

    private void handleException(SelectionKey key, Exception e) throws IOException {
        String errorMessage = switch (e.getClass().getSimpleName()) {
            case "IllegalPositionException" -> "!You cannot go there";
            case "IllegalIndexForInventoryException" -> "!There is no item in this slot of the backpack";
            case "NotEnoughLevelException" -> "!You don't have the level to use that";
            case "DeadPlayerCouldNotActException" -> "!You are dead so you cannot do anything, besides respawn";
            case "NotEnoughManaException" -> "!You don't have the mana to use that";
            case "BackPackIsFullException" -> "!Your backpack is full";
            case "CouldNotUseSpellOutOfBattleException" -> "!You can't use spell when out of battle";
            case "NoTreasureAtLocationException" -> "!There is no treasure here";
            case "NoEnemyToAttackException" -> "!There is no minion or hero to attack here";
            case "CouldOnlyCastASpellException" -> "!You can't cast anything apart from spell";
            default -> "";
        };
        sendMessageToClient(key, errorMessage);
    }

    public String executeCommand(Hero hero, String command)
        throws IllegalPositionException,
        IllegalIndexForInventoryException, NotEnoughLevelException, DeadPlayerCouldNotActException,
        NotEnoughManaException, BackPackIsFullException, CouldNotUseSpellOutOfBattleException,
        NoTreasureAtLocationException, NoEnemyToAttackException, CouldOnlyCastASpellException {
        String message = "";
        String[] partsOfCommand = command.split(" ");
        switch (partsOfCommand[0]) {
            case "up", "down", "right", "left":
                moveExecute(hero, partsOfCommand[0]);
                break;
            case "inventory", "level", "health", "xp", "pick", "mana":
                message = actionExecute(hero, partsOfCommand[0]);
                break;
            case "attack":
                int damage = hero.attack();
                message = String.format("!You did %d damage and you are at %d health", damage, hero.getCurrentHealth());
                break;
            case "use", "remove", "give", "spell":
                message = actionWithIndex(hero, partsOfCommand[0], partsOfCommand[1]);
                break;
            case "respawn":
                hero.respawn();
                message = "!You have been respawned";
                break;
            case "quit":
                message = "!You have quited";
                break;
            default:
                message = "!Invalid command. Please try again.";
        }
        return message.isEmpty() ? null : message;
    }

    private void moveExecute(Hero hero, String command)
        throws IllegalPositionException, DeadPlayerCouldNotActException {
        switch (command) {
            case "up":
                hero.moveUp();
                break;
            case "down":
                hero.moveDown();
                break;
            case "right":
                hero.moveRight();
                break;
            case "left":
                hero.moveLeft();
                break;
        }
    }

    private String actionExecute(Hero hero, String command) throws DeadPlayerCouldNotActException,
        NoTreasureAtLocationException, BackPackIsFullException {
        StringBuilder message = new StringBuilder();
        message.append("!");
        switch (command) {
            case "inventory":
                message.append("Things in the backpack").append(System.lineSeparator());
                for (Treasure treasure : hero.getBackPack()) {
                    message.append(treasure.toString()).append(System.lineSeparator());
                }
                break;
            case "level":
                message.append("Level: ").append(hero.getLevel());
                break;
            case "health":
                message.append("Health: ").append(hero.getCurrentHealth());
                break;
            case "xp":
                message.append("XP: ").append(hero.getCurrentXP());
                break;
            case "mana":
                message.append("Mana: ").append(hero.getCurrentMana());
                break;
            case "pick":
                hero.pickTreasure();
                message.append("You picked a treasure").append(System.lineSeparator());
                break;
        }

        return message.toString();
    }

    private String actionWithIndex(Hero hero, String command, String index)
        throws IllegalIndexForInventoryException, NotEnoughLevelException,
        CouldNotUseSpellOutOfBattleException, BackPackIsFullException, NotEnoughManaException,
        DeadPlayerCouldNotActException, NoEnemyToAttackException, CouldOnlyCastASpellException {

        StringBuilder message = new StringBuilder();
        message.append("!");
        switch (command) {
            case "inventory":
            case "use":
                hero.useItem(Integer.parseInt(index));
                break;
            case "remove":
                hero.removeItem(Integer.parseInt(index));
                break;
            case "give":
                hero.giveItem(Integer.parseInt(index));
                break;
            case "spell":
                int damage = hero.useSpell(Integer.parseInt(index));
                message.append(
                    String.format("You did %d damage and you are at %d mana", damage, hero.getCurrentMana()));
                break;
        }

        return message.toString();
    }

}
