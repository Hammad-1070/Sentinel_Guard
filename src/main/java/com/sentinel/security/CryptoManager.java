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


    private static final Dotenv dotenv = Dotenv.load();

    public static final String SYSTEM_KEY = dotenv.get("SYSTEM_KEY");


    public static String generateMasterKey() {
        try {

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");

            keyGen.init(256);

            SecretKey secretKey = keyGen.generateKey();

            return Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (Exception e) {
            System.out.println("Critical Error: Cryptographic engine failed to start.");
            return null;
        }
    }
    public static String encrypt(String plainText, String base64Key) {
        try {

            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));

            byte[] finalPackage = new byte[iv.length + encryptedText.length];
            System.arraycopy(iv, 0, finalPackage, 0, iv.length);
            System.arraycopy(encryptedText, 0, finalPackage, iv.length, encryptedText.length);

            return Base64.getEncoder().encodeToString(finalPackage);

        } catch (Exception e) {
            System.out.println("Encryption Error: " + e.getMessage());
            return null;
        }
    }


    public static String decrypt(String encryptedPackage, String base64Key) {
        try {

            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            byte[] decodedPackage = Base64.getDecoder().decode(encryptedPackage);
            byte[] iv = Arrays.copyOfRange(decodedPackage, 0, 16);
            byte[] encryptedText = Arrays.copyOfRange(decodedPackage, 16, decodedPackage.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] originalText = cipher.doFinal(encryptedText);
            return new String(originalText, "UTF-8");

        } catch (Exception e) {
            System.out.println("Decryption Error: Wrong key or corrupted data.");
            return null;
        }
    }
}
