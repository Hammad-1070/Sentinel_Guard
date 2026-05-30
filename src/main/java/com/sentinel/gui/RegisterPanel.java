package com.sentinel.gui;

import com.sentinel.security.AuthManager;
import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private SentinelFrame mainMonitor;

    public RegisterPanel(SentinelFrame mainMonitor) {
        this.mainMonitor = mainMonitor;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITLE ---
        JLabel titleLabel = new JLabel("NEW AGENT REGISTRATION");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // --- LAYER 1: CREDENTIALS ---
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Desired Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Secure Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // --- LAYER 2: SECURITY QUESTION ---
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Custom Security Question:"), gbc);
        JTextField questionField = new JTextField(15);
        gbc.gridx = 1;
        add(questionField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("Security Answer:"), gbc);
        JTextField answerField = new JTextField(15);
        gbc.gridx = 1;
        add(answerField, gbc);

        // --- BUTTONS ---
        JButton submitButton = new JButton("AUTHORIZE & FORGE KEYS");
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        add(submitButton, gbc);

        JButton backButton = new JButton("Abort (Return to Login)");
        gbc.gridy = 6;
        add(backButton, gbc);


        submitButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            String question = questionField.getText();
            String answer = answerField.getText();


            if (user.isEmpty() || pass.isEmpty() || question.isEmpty() || answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Error: All fields are strictly required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            boolean success = AuthManager.registerUser(user, pass, question, answer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Success: Agent clearance granted. Cryptographic keys forged.");

                mainMonitor.switchPanel(new LoginPanel(mainMonitor));
            } else {
                JOptionPane.showMessageDialog(this, "Error: Registration failed. Username may already be compromised.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {

            mainMonitor.switchPanel(new LoginPanel(mainMonitor));
        });
    }
}