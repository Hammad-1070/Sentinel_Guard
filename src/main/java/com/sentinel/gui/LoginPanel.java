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

                    mainMonitor.switchPanel(new VaultPanel(mainMonitor, user));
                } else {
                    JOptionPane.showMessageDialog(this, "Intrusion Detected: Incorrect password or security answer.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(e -> {
            System.out.println("Switching to Registration channel...");
        });

        registerButton.addActionListener(e -> {
            // Switch the TV Channel to the Registration Screen!
            mainMonitor.switchPanel(new RegisterPanel(mainMonitor));
        });
    }
}