package bg.sofia.uni.fmi.mjt.photoalbum;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {

    private static final double NANO_TO_MS_SECS = 1_000_000.0;
    private int imageProcessorsCount;
    public BlockingQueue<Image> images;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imageProcessorsCount = imageProcessorsCount;
        images = new ArrayBlockingQueue<>(imageProcessorsCount);
    }


    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        long startTime = System.nanoTime();

        List<Thread> producers;
        producers = startProducing(sourceDirectory);

        List<Thread> consumers;
        consumers = loadConsumers(outputDirectory);

        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        loadPoisonPills();

        for (Thread consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long endTime = System.nanoTime();
        System.out.println((endTime - startTime) / NANO_TO_MS_SECS + " ms.");
    }

    private List<Thread> startProducing(String sourceDirectory) {
        Path sourceFile = Path.of(sourceDirectory);
        List<Thread> producers = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceFile, "{*.png,*.jpg,*.jpeg}")) {
            for (Path imagePath : stream) {
                Thread producerThread = new Thread(new Producer("pr", images, imagePath));
                producers.add(producerThread);
                producerThread.start();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load directory", e);
        }

        return producers;
    }

    private List<Thread> loadConsumers(String outputDirectory) {
        Consumer.setOutputDirectory(outputDirectory);

        Consumer[] consumers = new Consumer[imageProcessorsCount];
        for (int i = 0; i < imageProcessorsCount; i++) {
            consumers[i] = new Consumer("ct", images);
        }

        List<Thread> consumerThreads = new ArrayList<>(imageProcessorsCount);
        for (int i = 0; i < imageProcessorsCount; i++) {
            Thread consumerThread = new Thread(consumers[i]);
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }

        return consumerThreads;
    }

    private void loadPoisonPills() {
        for (int i = 0; i < imageProcessorsCount; i++) {
            try {
                images.put(new PoisonPill("PoisionPill"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
