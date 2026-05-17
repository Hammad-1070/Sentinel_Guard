package com.sentinel.security;

import io.github.cdimascio.dotenv.Dotenv;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;;

public class CryptoManager {

    // 1. Load the hidden .env file
    private static final Dotenv dotenv = Dotenv.load();

    // 2. Extract the System Key into memory safely
    public static final String SYSTEM_KEY = dotenv.get("SYSTEM_KEY");



    /**
     * The Key Forge: Creates a brand new, highly secure AES-256 Master Key.
     */
    public static String generateMasterKey() {
        try {
            // 1. Request the AES Machine
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");

            // 2. Set the power level to Maximum (256-bit)
            keyGen.init(256);

            // 3. Forge the key
            SecretKey secretKey = keyGen.generateKey();

            // 4. Translate it from binary to text
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (Exception e) {
            System.out.println("Critical Error: Cryptographic engine failed to start.");
            return null;
        }
    }
    public static String encrypt(String plainText, String base64Key) {
        try {
            // 1. Rebuild the physical Key from the text string
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            // 2. Generate the IV (The "Salt" for encryption)
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 3. Start the Cipher Machine
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // 4. Encrypt the actual text
            byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // 5. Package the IV and Encrypted Text together into one safe box
            byte[] finalPackage = new byte[iv.length + encryptedText.length];
            System.arraycopy(iv, 0, finalPackage, 0, iv.length);
            System.arraycopy(encryptedText, 0, finalPackage, iv.length, encryptedText.length);

            // 6. Translate the binary package into a readable string
            return Base64.getEncoder().encodeToString(finalPackage);

        } catch (Exception e) {
            System.out.println("Encryption Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * The Unlocking Mechanism: Opens the vault using the exact same Master Key.
     */
    public static String decrypt(String encryptedPackage, String base64Key) {
        try {
            // 1. Rebuild the physical Key
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            // 2. Open the package and separate the IV from the Encrypted Text
            byte[] decodedPackage = Base64.getDecoder().decode(encryptedPackage);
            byte[] iv = Arrays.copyOfRange(decodedPackage, 0, 16);
            byte[] encryptedText = Arrays.copyOfRange(decodedPackage, 16, decodedPackage.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 3. Put the Cipher Machine in Reverse
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // 4. Decrypt and return the original text
            byte[] originalText = cipher.doFinal(encryptedText);
            return new String(originalText, "UTF-8");

        } catch (Exception e) {
            System.out.println("Decryption Error: Wrong key or corrupted data.");
            return null;
        }
    }
}
