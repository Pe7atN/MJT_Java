package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private BlockingQueue<Image> blockingQueue;
    private Path imagePath;
    private String name;
    private static int counter = 0;

    public Producer(String name, BlockingQueue<Image> blockingQueue, Path imagePath) {
        this.name = name + "-" + counter;
        this.blockingQueue = blockingQueue;
        this.imagePath = imagePath;
        counter++;
    }

    @Override
    public void run() {
        System.out.println(this.getThisName() + " started");
        Image imageToInsert = loadImage(imagePath);
        try {
            blockingQueue.put(imageToInsert);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    private String getThisName() {
        return name;
    }
}
