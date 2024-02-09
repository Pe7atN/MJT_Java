package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private BlockingQueue<Image> blockingQueue;
    private static String outputDirectory;
    private String name;
    private static int counter = 0;

    public static void setOutputDirectory(String path) {
        outputDirectory = path;
    }

    public Consumer(String name, BlockingQueue<Image> blockingQueue) {
        this.name = name + "-" + counter;
        this.blockingQueue = blockingQueue;
        counter++;
    }

    @Override
    public void run() {
        System.out.println(this.getThisName() + " started");
        while (true) {
            Image currentImage = null;
            try {
                currentImage = blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (currentImage instanceof PoisonPill) {
                break;
            }

            Image blackAndWhite = convertToBlackAndWhite(currentImage);
            saveImage(blackAndWhite, outputDirectory);
        }

        System.out.println(this.getThisName() + " ended");
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(image.data.getWidth(),
                image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    public static void saveImage(Image image, String filePath) {
        try {
            File outputfile = new File(filePath + "\\" + image.name);
            if (!outputfile.exists()) {
                boolean created = outputfile.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create output folder: " + outputfile.getAbsolutePath());
                }
            }

            ImageIO.write(image.data, "png", outputfile);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to save image to file %s", filePath), e);
        }
    }

    private String getThisName() {
        return name;
    }
}
