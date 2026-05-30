package org.example;

import com.sentinel.security.AuthManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("      SENTINEL GUARD - SYSTEM TERMINAL   ");
        System.out.println("=========================================");

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

                String question = com.sentinel.security.AuthManager.getSecurityQuestion(user);

                if (question == null) {
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

                    boolean isLoggedIn = com.sentinel.security.AuthManager.loginUser(user, pass, answer);

                    if (isLoggedIn) {
                        // --- UPGRADED VAULT TERMINAL SESSION LOOP ---
                        while (true) {
                            System.out.println("\n--- VAULT TERMINAL (" + user + ") ---");
                            System.out.println("1. Write a new secure note");
                            System.out.println("2. Read all secure notes");
                            System.out.println("3. Delete a secure note"); // NEW OPTION
                            System.out.println("4. Log out");              // SHIFTED OPTION
                            System.out.print("Command: ");

                            String sessionChoice = scanner.nextLine();

                            if (sessionChoice.equals("1")) {
                                System.out.print("Note Title: ");
                                String title = scanner.nextLine();
                                System.out.print("Classified Content: ");
                                String content = scanner.nextLine();

                                com.sentinel.core.VaultManager.saveNote(user, title, content);

                            } else if (sessionChoice.equals("2")) {
                                com.sentinel.core.VaultManager.readNotes(user);

                            } else if (sessionChoice.equals("3")) {
                                System.out.print("Enter the Note ID to incinerate: ");
                                try {
                                    int targetId = Integer.parseInt(scanner.nextLine());
                                    com.sentinel.core.VaultManager.deleteNote(user, targetId);
                                } catch (NumberFormatException e) {
                                    System.out.println("Error: Please enter a valid number.");
                                }

                            } else if (sessionChoice.equals("4")) {
                                System.out.println("Logging out... Vault sealed.");
                                break;
                            } else {
                                System.out.println("Unrecognized command.");
                            }
                        }
                    }
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
                break;

            } else if (choice.equals("5")) {
                System.out.println("\n--- CLASSIFIED ENCRYPTION TEST ---");
                System.out.print("Type a highly classified message: ");
                String secretMessage = scanner.nextLine();

                String temporaryKey = com.sentinel.security.CryptoManager.generateMasterKey();
                System.out.println("\n[+] Forged Key: " + temporaryKey);

                String encryptedCypherText = com.sentinel.security.CryptoManager.encrypt(secretMessage, temporaryKey);
                System.out.println("[+] Encrypted Vault: " + encryptedCypherText);

                String decryptedOriginal = com.sentinel.security.CryptoManager.decrypt(encryptedCypherText, temporaryKey);
                System.out.println("[+] Decrypted Result: " + decryptedOriginal);

            } else {
                System.out.println("Error: Unrecognized command.");
            }
        }

        scanner.close();
    }
}