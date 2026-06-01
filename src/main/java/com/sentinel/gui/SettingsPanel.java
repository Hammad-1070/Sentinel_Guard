package com.sentinel.gui;

import com.sentinel.security.AuthManager;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private SentinelFrame mainMonitor;
    private String activeUser;

    public SettingsPanel(SentinelFrame mainMonitor, String username) {
        this.mainMonitor = mainMonitor;
        this.activeUser = username;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITLE ---
        JLabel titleLabel = new JLabel("SYSTEM SETTINGS: " + activeUser.toUpperCase());
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // --- INPUT FIELDS ---
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Current Password:"), gbc);
        JPasswordField currentPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(currentPassField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("New Password:"), gbc);
        JPasswordField newPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(newPassField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Confirm New Password:"), gbc);
        JPasswordField confirmPassField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPassField, gbc);

        // --- BUTTONS ---
        JButton updateButton = new JButton("EXECUTE PASSWORD ROTATION");
        updateButton.setForeground(new Color(200, 50, 50)); // Warning Red
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(updateButton, gbc);

        JButton backButton = new JButton("Abort (Return to Vault)");
        gbc.gridy = 5;
        add(backButton, gbc);

        // --- CLICK EVENTS ---
        updateButton.addActionListener(e -> {
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Error: All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Error: New passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send to Backend
            boolean success = AuthManager.changePassword(activeUser, currentPass, newPass);

            if (success) {
                JOptionPane.showMessageDialog(this, "Success: Cryptographic credentials updated.\nPlease log in again with your new password.");
                // Kick them out to the login screen for safety
                mainMonitor.switchPanel(new LoginPanel(mainMonitor));
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Current password is incorrect.", "Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            mainMonitor.switchPanel(new VaultPanel(mainMonitor, activeUser));
        });
    }
}