package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {

    private SecretKey secretKey;
    private static final int KILOBYTE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES";

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (OutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            throw new CipherException("Error during encryption", e);
        }
    }

    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (OutputStream decryptedOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[KILOBYTE];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    decryptedOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            throw new CipherException("Error during encryption", e);
        }
    }
}
