package bg.sofia.uni.fmi.mjt.dungeon.server;

import bg.sofia.uni.fmi.mjt.dungeon.exception.CouldNotConnectException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeroClient {
    private static final String EXCEPTIONS_LOG = "Exception_Log_Client.txt";
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;

    private ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private AtomicBoolean isOn;

    public static void main(String[] args) throws CouldNotConnectException {
        HeroClient gameClient = new HeroClient();
        gameClient.start();
    }

    private void start() throws CouldNotConnectException {
        isOn = new AtomicBoolean(true);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            printCommands();

            Thread sendThread = createSendThread(scanner, socketChannel);
            Thread receiveThread = createReceiveThread(scanner, socketChannel);

            sendThread.start();
            receiveThread.start();

            sendThread.join();
            receiveThread.join();
        } catch (IOException | InterruptedException e) {
            logException(e);
            throw new CouldNotConnectException(
                "Unable to connect to the server." +
                    " Try again later or contact administrator by providing the logs in " +
                    Paths.get(EXCEPTIONS_LOG).toAbsolutePath(),
                e);
        }
    }

    private Thread createSendThread(Scanner scanner, SocketChannel socketChannel) {
        return new Thread(() -> {
            while (isOn.get()) {
                String message = scanner.nextLine(); // read a line from the console
                System.out.println();
                if ("quit".equals(message)) {
                    isOn.set(false);
                }

                try {
                    sendToServer(socketChannel, message);
                } catch (IOException e) {
                    logException(e);
                    throw new UncheckedIOException(
                        "Unable to send data to the server." +
                            " Try again later or contact administrator by providing the logs in " +
                            Paths.get(EXCEPTIONS_LOG).toAbsolutePath(),
                        e);
                }
            }
        });
    }

    private Thread createReceiveThread(Scanner scanner, SocketChannel socketChannel) {
        return new Thread(() -> {
            try {
                while (isOn.get()) {
                    receiveFromServer(socketChannel);
                }
            } catch (IOException e) {
                logException(e);
                throw new UncheckedIOException(
                    "Unable to receive data to the server." +
                        " Try again later or contact administrator by providing the logs in " +
                        Paths.get(EXCEPTIONS_LOG).toAbsolutePath(),
                    e);
            }
        });
    }

    private void sendToServer(SocketChannel socketChannel, String message) throws IOException {
        //message += System.lineSeparator();
        sendBuffer.clear();
        sendBuffer.put(message.getBytes());
        sendBuffer.flip();
        socketChannel.write(sendBuffer);
    }

    private void receiveFromServer(SocketChannel socketChannel) throws IOException {
        receiveBuffer.clear();
        socketChannel.read(receiveBuffer);
        receiveBuffer.flip();
        if (receiveBuffer.get(0) == '!') {
            receiveBuffer.get();
            byte[] remainingBytes = new byte[receiveBuffer.remaining()];
            receiveBuffer.get(remainingBytes);
            String message = new String(remainingBytes, StandardCharsets.UTF_8);
            System.out.println(message);
            return;
        }
        char[][] receivedMatrix = receiveDungeon(receiveBuffer);
        printDungeon(receivedMatrix, receivedMatrix.length, receivedMatrix[0].length);
    }

    private static char[][] receiveDungeon(ByteBuffer byteBuffer) {
        int rows = byteBuffer.getChar();
        int cols = byteBuffer.getChar();
        char[][] restoredMatrix = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                restoredMatrix[i][j] = byteBuffer.getChar();
            }
        }

        return restoredMatrix;
    }

    private static void printDungeon(char[][] a, int rows, int cols) {
        System.out.println("------Dungeon-------");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------");
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

    private void printCommands() {
        System.out.println("Connected to the server.");
        System.out.println("---List with commands---");
        System.out.println("Commands: up - move up");
        System.out.println("Commands: down - move down");
        System.out.println("Commands: right - move right");
        System.out.println("Commands: left - move left");
        System.out.println("Commands: pick - pick up treasure");
        System.out.println("Commands: inventory - check inventory");
        System.out.println("Commands: level - check level");
        System.out.println("Commands: xp - check xp");
        System.out.println("Commands: health - check health");
        System.out.println("Commands: give (index) - give item");
        System.out.println("Commands: use (index) - use item");
        System.out.println("Commands: remove (index) - remove item");
        System.out.println("Commands: attack - attack");
        System.out.println("Commands: quit - quit the game");
    }

}
