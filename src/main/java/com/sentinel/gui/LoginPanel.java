package com.sentinel.gui;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private SentinelFrame mainMonitor;

    public LoginPanel(SentinelFrame mainMonitor) {
        this.mainMonitor = mainMonitor;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Adds padding between the buttons and text
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("SENTINEL GUARD");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);


        gbc.gridwidth = 1; // Reset to 1 column
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);


        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("ACCESS SYSTEM");
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(loginButton, gbc);


        JButton registerButton = new JButton("Create New User (Register)");
        gbc.gridy = 4;
        add(registerButton, gbc);


        loginButton.addActionListener(e -> {

            String user = usernameField.getText();

            String pass = new String(passwordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Error: Credentials cannot be blank.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String question = com.sentinel.security.AuthManager.getSecurityQuestion(user);

            if (question == null) {

                JOptionPane.showMessageDialog(this, "Error: Invalid username or password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }


            String answer = JOptionPane.showInputDialog(this,
                    "Layer 2 Verification Required:\n" + question,
                    "Security Clearance",
                    JOptionPane.QUESTION_MESSAGE);


            if (answer != null) {


                boolean isLoggedIn = com.sentinel.security.AuthManager.loginUser(user, pass, answer);

                if (isLoggedIn) {

                    mainMonitor.switchPanel(new LoadingPanel(mainMonitor, user));
                } else {
                    JOptionPane.showMessageDialog(this, "Intrusion Detected: Incorrect password or security answer.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(e -> {
            System.out.println("Switching to Registration channel...");
        });

        // --- 6. THE FORGOT PASSWORD LINK ---
        JButton forgotPassButton = new JButton("Emergency Override (Forgot Password)");
        forgotPassButton.setForeground(new Color(200, 150, 0)); // Warning Orange
        gbc.gridy = 5;
        add(forgotPassButton, gbc);

        // --- 7. THE ABOUT SYSTEM BUTTON ---
        JButton aboutButton = new JButton("About Sentinel Guard");
        aboutButton.setForeground(new Color(0, 150, 255)); // Professional Cyber Blue
        gbc.gridy = 6;
        add(aboutButton, gbc);


        registerButton.addActionListener(e -> {
            // Switch the TV Channel to the Registration Screen!
            mainMonitor.switchPanel(new RegisterPanel(mainMonitor));
        });

        forgotPassButton.addActionListener(e -> {
            // Step 1: Ask for the username
            String targetUser = JOptionPane.showInputDialog(this, "Enter your Username for recovery:");
            if (targetUser == null || targetUser.trim().isEmpty()) return;

            // Step 2: Fetch their specific security question from the database
            String question = com.sentinel.security.AuthManager.getSecurityQuestion(targetUser);
            if (question == null) {
                JOptionPane.showMessageDialog(this, "Error: Agent not found in the registry.", "Recovery Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 3: Ask the question
            String answer = JOptionPane.showInputDialog(this,
                    "Identity Verification Required:\n" + question,
                    "Emergency Recovery",
                    JOptionPane.WARNING_MESSAGE);

            if (answer == null || answer.trim().isEmpty()) return;

            // Step 4: Build a secure popup for the new password
            JPasswordField newPassField = new JPasswordField(10);
            JPasswordField confirmPassField = new JPasswordField(10);

            JPanel myPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            myPanel.add(new JLabel("New Password:"));
            myPanel.add(newPassField);
            myPanel.add(new JLabel("Confirm Password:"));
            myPanel.add(confirmPassField);

            int result = JOptionPane.showConfirmDialog(this, myPanel, "Forge New Clearance Codes", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());

                if (newPass.isEmpty() || !newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Error: Passwords cannot be empty and must match.", "Recovery Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Step 5: Send it to the Backend!
                boolean success = com.sentinel.security.AuthManager.recoverPassword(targetUser, answer, newPass);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Success: Credentials rotated. You may now log in.", "Override Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied: Incorrect security answer.", "Intrusion Detected", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        aboutButton.addActionListener(e -> {
            String aboutText =
                    "=========================================\n" +
                            "               SENTINEL GUARD\n" +
                            "     Military-Grade Encrypted Desktop Vault\n" +
                            "=========================================\n\n" +
                            "[ CORE ARCHITECTURE ]\n" +
                            "• Engine: Java (Swing GUI Asynchronous)\n" +
                            "• Database: MySQL Relational Database\n" +
                            "• Deployment: Maven Automated Pipeline & Launch4j\n\n" +
                            "[ CRYPTOGRAPHIC PROTOCOLS ]\n" +
                            "• Master Vault: AES-256 Envelope Encryption\n" +
                            "• Credential Storage: BCrypt Hash Engine\n" +
                            "• Multi-Factor: Layer 2 Security Question Protocol\n\n" +
                            "=========================================\n" +
                            " Lead Architect & Backend Developer:\n" +
                            " MUHAMMAD HAMMAD SALEEM\n" +
                            "=========================================";

            JOptionPane.showMessageDialog(this,
                    aboutText,
                    "System Specifications",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}