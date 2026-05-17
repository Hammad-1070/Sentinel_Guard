package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.package org.example; // Leave this as whatever your package name currently is

import com.sentinel.security.AuthManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // The Scanner is our tool to read text from the keyboard
        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("      SENTINEL GUARD - SYSTEM TERMINAL   ");
        System.out.println("=========================================");

        // An infinite loop to keep the menu running until we type '3'
        while (true) {
            System.out.println("\n1. Register New User");
            System.out.println("2. System Login");
            System.out.println("3. Shut Down System");
            System.out.print("Command: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter new username: ");
                String user = scanner.nextLine();
                System.out.print("Enter new password: ");
                String pass = scanner.nextLine();

                // Call the gateway we built!
                AuthManager.registerUser(user, pass);

            } else if (choice.equals("2")) {
                System.out.print("Enter username: ");
                String user = scanner.nextLine();
                System.out.print("Enter password: ");
                String pass = scanner.nextLine();

                // Call the bouncer we built!
                AuthManager.loginUser(user, pass);

            } else if (choice.equals("3")) {
                System.out.println("Terminating Sentinel Guard secure session...");
                break; // This breaks the infinite loop and closes the app
            } else {
                System.out.println("Error: Unrecognized command.");
            }
        }

        scanner.close(); // Always close your tools when done!
    }
}