package com.sentinel.gui;

import com.sentinel.core.VaultManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VaultPanel extends JPanel {

    private SentinelFrame mainMonitor;
    private String activeUser;
    private JTextArea terminalDisplay;

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


        refreshVaultData();


        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // 1 row, 3 columns

        JButton writeNoteBtn = new JButton("Write Classified Note");
        JButton deleteNoteBtn = new JButton("Incinerate Note");
        JButton logoutBtn = new JButton("Seal Vault (Logout)");

        controlPanel.add(writeNoteBtn);
        controlPanel.add(deleteNoteBtn);
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

        logoutBtn.addActionListener(e -> {
            // Destroy the screen and go back to login
            mainMonitor.switchPanel(new LoginPanel(mainMonitor));
        });
    }


    private void refreshVaultData() {
        terminalDisplay.setText(""); // Clear the screen

        List<String> notes = VaultManager.readNotesForGUI(activeUser);

        if (notes.isEmpty()) {
            terminalDisplay.append("Vault is completely empty.\nNo classified documents found.");
        } else {
            for (String note : notes) {
                terminalDisplay.append(note + "\n\n");
            }
        }

        // Scroll back to the top automatically
        terminalDisplay.setCaretPosition(0);
    }
}