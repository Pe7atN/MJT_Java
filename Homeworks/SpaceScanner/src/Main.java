import bg.sofia.uni.fmi.mjt.space.MJTSpaceScanner;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class Main {

    private static final String ENCRYPTION_ALGORITHM = "AES"; // //  Advanced Encryption Standard
    private static final int KEY_SIZE_IN_BITS = 128; // Key sizes like 192 or 256 might not be available on all systems

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, CipherException {

        Reader readerM = new FileReader("all-missions-from-1957.csv");
        Reader readerR = new FileReader("all-rockets-from-1957.csv");
        SecretKey secretKey = generateSecretKey();
        MJTSpaceScanner scanner = new MJTSpaceScanner(readerM,
                readerR, secretKey);

        ByteArrayOutputStream encryptionDestination = new ByteArrayOutputStream();
        scanner.saveMostReliableRocket(encryptionDestination, LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 12, 1));
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }
}