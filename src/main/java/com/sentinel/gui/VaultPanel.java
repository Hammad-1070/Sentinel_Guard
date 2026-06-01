package com.sentinel.gui;

import com.sentinel.core.VaultManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VaultPanel extends JPanel {

    private SentinelFrame mainMonitor;
    private String activeUser;
    private JTextArea terminalDisplay;

    // --- NEW ANIMATION VARIABLES ---
    private Timer typewriterTimer;
    private int charIndex = 0;
    private String targetText = "";

    public VaultPanel(SentinelFrame mainMonitor, String username) {
        this.mainMonitor = mainMonitor;
        this.activeUser = username;

        setLayout(new BorderLayout(10, 10)); // 10px padding between zones
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the edges

        // --- 1. THE HEADER (NORTH) ---
        JLabel headerLabel = new JLabel("SECURE VAULT TERMINAL | ACTIVE CLEARANCE: " + activeUser.toUpperCase());
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 200, 0)); // Hacker Green text
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // --- 2. THE DATA DISPLAY (CENTER) ---
        terminalDisplay = new JTextArea();
        terminalDisplay.setEditable(false); // Make it read-only
        terminalDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalDisplay.setLineWrap(true);
        terminalDisplay.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(terminalDisplay);
        add(scrollPane, BorderLayout.CENTER);

        // Load the notes immediately when the screen opens!
        refreshVaultData();

        // --- 3. THE CONTROL CONSOLE (SOUTH) ---
        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 10, 0)); // 4 Columns!

        JButton writeNoteBtn = new JButton("Write Classified Note");
        JButton deleteNoteBtn = new JButton("Incinerate Note");
        JButton settingsBtn = new JButton("System Settings"); // --- NEW BUTTON ---
        JButton logoutBtn = new JButton("Seal Vault (Logout)");

        controlPanel.add(writeNoteBtn);
        controlPanel.add(deleteNoteBtn);
        controlPanel.add(settingsBtn); // --- ADDED TO PANEL ---
        controlPanel.add(logoutBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // --- CLICK EVENTS ---

        writeNoteBtn.addActionListener(e -> {
            // Popups to gather the title and content
            String title = JOptionPane.showInputDialog(this, "Enter Note Title:");
            if (title == null || title.trim().isEmpty()) return;

            String content = JOptionPane.showInputDialog(this, "Enter Classified Content:");
            if (content == null || content.trim().isEmpty()) return;

            boolean success = VaultManager.saveNote(activeUser, title, content);
            if (success) {
                JOptionPane.showMessageDialog(this, "Note securely vaulted.");
                refreshVaultData(); // Reload the screen to show the new note!
            } else {
                JOptionPane.showMessageDialog(this, "Encryption failure.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteNoteBtn.addActionListener(e -> {
            String idString = JOptionPane.showInputDialog(this, "Enter the Note ID to incinerate:");
            if (idString == null || idString.trim().isEmpty()) return;

            try {
                int targetId = Integer.parseInt(idString);
                boolean success = VaultManager.deleteNote(activeUser, targetId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Target obliterated.");
                    refreshVaultData(); // Reload the screen to remove the ghost
                } else {
                    JOptionPane.showMessageDialog(this, "Target not found or access denied.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- NEW WIRE: ROUTE TO SETTINGS PANEL ---
        settingsBtn.addActionListener(e -> {
            mainMonitor.switchPanel(new SettingsPanel(mainMonitor, activeUser));
        });

        logoutBtn.addActionListener(e -> {
            // Destroy the screen and go back to login
            mainMonitor.switchPanel(new LoginPanel(mainMonitor));
        });
    }

    private void refreshVaultData() {
        // 1. Gather all the notes into one massive String first
        List<String> notes = VaultManager.readNotesForGUI(activeUser);
        StringBuilder fullText = new StringBuilder();

        if (notes.isEmpty()) {
            fullText.append("SYSTEM ALARM: Vault is completely empty.\nNo classified documents found.");
        } else {
            for (String note : notes) {
                fullText.append(note).append("\n\n");
            }
        }

        // 2. Send the massive String to the animation engine
        animateText(fullText.toString());
    }

    /**
     * THE MATRIX ENGINE: Types text asynchronously without freezing the GUI.
     */
    private void animateText(String text) {
        // If an old animation is still running, kill it before starting a new one
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }

        terminalDisplay.setText(""); // Wipe the screen clean
        targetText = text;
        charIndex = 0;

        // Create a clock that ticks every 15 milliseconds (Lower number = faster typing)
        typewriterTimer = new Timer(15, e -> {
            if (charIndex < targetText.length()) {
                // Drop one letter onto the screen
                terminalDisplay.append(String.valueOf(targetText.charAt(charIndex)));
                charIndex++;

                // Force the screen to auto-scroll down as it types
                terminalDisplay.setCaretPosition(terminalDisplay.getDocument().getLength());
            } else {
                // The whole document is finished typing. Stop the clock.
                ((Timer) e.getSource()).stop();
            }
        });

        // Fire the engine!
        typewriterTimer.start();
    }
}