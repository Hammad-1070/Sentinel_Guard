package org.example;

import com.sentinel.security.AuthManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // The Scanner is our tool to read text from the keyboard
        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("      SENTINEL GUARD - SYSTEM TERMINAL   ");
        System.out.println("=========================================");

        // A single loop keeps the menu running until we type '4'
        while (true) {
            System.out.println("\n1. Register New User");
            System.out.println("2. System Login (2FA)");
            System.out.println("3. Delete Account");
            System.out.println("4. Shut Down System");
            System.out.println("5. [DEV] Test Encryption Engine");
            System.out.println("99. [DEV] Generate Master Key");
            System.out.print("Command: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter new username: ");
                String user = scanner.nextLine();
                System.out.print("Enter new password: ");
                String pass = scanner.nextLine();

                System.out.println("--- Setup Security Question ---");
                System.out.print("Type a custom security question (e.g., First pet's name?): ");
                String question = scanner.nextLine();
                System.out.print("Answer: ");
                String answer = scanner.nextLine();

                AuthManager.registerUser(user, pass, question, answer);

            } else if (choice.equals("2")) {
                System.out.print("Enter username: ");
                String user = scanner.nextLine();

                // Fetch the question BEFORE asking for the password
                String question = AuthManager.getSecurityQuestion(user);

                if (question == null) {
                    // Security rule: If user doesn't exist, don't tell the hacker! Just pretend it failed.
                    System.out.print("Enter password: ");
                    scanner.nextLine();
                    System.out.println("Error: Invalid username or password.");
                } else {
                    System.out.print("Enter password: ");
                    String pass = scanner.nextLine();

                    System.out.println("--- Security Question ---");
                    System.out.println(question);
                    System.out.print("Answer: ");
                    String answer = scanner.nextLine();

                    AuthManager.loginUser(user, pass, answer);
                }

            } else if (choice.equals("3")) {
                System.out.println("--- DANGER: ACCOUNT DELETION ---");
                System.out.print("Enter the username to delete: ");
                String user = scanner.nextLine();
                System.out.print("Enter your password to verify identity: ");
                String pass = scanner.nextLine();

                System.out.print("Are you absolutely sure? This cannot be undone. (y/n): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("y")) {
                    AuthManager.deleteUser(user, pass);
                } else {
                    System.out.println("Deletion aborted. Account is safe.");
                }

            } else if (choice.equals("99")) {
                System.out.println("--- FORGING AES-256 MASTER KEY ---");
                String newKey = com.sentinel.security.CryptoManager.generateMasterKey();
                System.out.println("Your Key: " + newKey);
                System.out.println("Warning: Whoever holds this string controls the encrypted data.");

            } else if (choice.equals("4")) {
                System.out.println("Terminating Sentinel Guard secure session...");
                break; // Exits the while loop and shuts down

            } else if (choice.equals("5")) {
                System.out.println("\n--- CLASSIFIED ENCRYPTION TEST ---");
                System.out.print("Type a highly classified message: ");
                String secretMessage = scanner.nextLine();

                // 1. Forge a temporary key
                String temporaryKey = com.sentinel.security.CryptoManager.generateMasterKey();
                System.out.println("\n[+] Forged Key: " + temporaryKey);

                // 2. Lock the message
                String encryptedCypherText = com.sentinel.security.CryptoManager.encrypt(secretMessage, temporaryKey);
                System.out.println("[+] Encrypted Vault: " + encryptedCypherText);

                // 3. Unlock the message
                String decryptedOriginal = com.sentinel.security.CryptoManager.decrypt(encryptedCypherText, temporaryKey);
                System.out.println("[+] Decrypted Result: " + decryptedOriginal);}

            else {
                System.out.println("Error: Unrecognized command.");
            }
        }

        scanner.close(); // Clean up resource link
    }
}
