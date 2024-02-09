import bg.sofia.uni.fmi.mjt.photoalbum.ParallelMonochromeAlbumCreator;

public class Main {
    public static void main(String[] args) throws Exception {

        ParallelMonochromeAlbumCreator album = new ParallelMonochromeAlbumCreator(30);
        album.processImages("sourceImages", "outputImages");
        System.out.println("Process is done");

    }
}