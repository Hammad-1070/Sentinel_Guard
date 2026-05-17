package com.sentinel.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class CryptoManager {

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
}