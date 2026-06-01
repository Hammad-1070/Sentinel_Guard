package com.sentinel.gui;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private SentinelFrame mainMonitor;
    private String activeUser;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Timer bootTimer;
    private int progress = 0;

    public LoadingPanel(SentinelFrame mainMonitor, String username) {
        this.mainMonitor = mainMonitor;
        this.activeUser = username;

        // --- LAYOUT SETUP ---
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. THE TITLE ---
        JLabel titleLabel = new JLabel("INITIATING SECURE HANDSHAKE...");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 255, 0)); // Hacker Green
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        add(titleLabel, gbc);

        // --- 2. THE PROGRESS BAR ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // Shows the % text
        progressBar.setForeground(new Color(0, 255, 0));
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setFont(new Font("Monospaced", Font.BOLD, 16));

        // Make the bar physically larger
        progressBar.setPreferredSize(new Dimension(400, 30));
        gbc.gridy = 1;
        add(progressBar, gbc);

        // --- 3. THE HACKER TERMINAL TEXT ---
        statusLabel = new JLabel("Establishing secure connection...");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        add(statusLabel, gbc);

        // --- THE ANIMATION ENGINE ---
        // Ticks every 40 milliseconds.
        bootTimer = new Timer(40, e -> {
            progress += 2; // Increase bar by 2% every tick
            progressBar.setValue(progress);

            // Swap the text based on how far the bar has loaded
            if (progress == 10) {
                statusLabel.setText("Bypassing external firewalls...");
            } else if (progress == 30) {
                statusLabel.setText("Connecting to encrypted database...");
            } else if (progress == 50) {
                statusLabel.setText("Verifying BCrypt Cryptographic Hashes...");
            } else if (progress == 75) {
                statusLabel.setText("Decrypting AES-256 Master Vault...");
            } else if (progress == 95) {
                statusLabel.setText("Access Granted.");
                statusLabel.setForeground(new Color(0, 255, 0)); // Turn text green at the end!
            }

            // When the bar hits 100%, kill the timer and open the Vault!
            if (progress >= 100) {
                bootTimer.stop();
                mainMonitor.switchPanel(new VaultPanel(mainMonitor, activeUser));
            }
        });

        // Fire the engine immediately when the panel opens
        bootTimer.start();
    }
}