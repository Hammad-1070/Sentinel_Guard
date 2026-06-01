package com.sentinel.gui;

import com.sentinel.core.VaultManager;
import com.sentinel.security.SecurityLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.util.List;

public class VaultPanel extends JPanel {

    private SentinelFrame mainMonitor;
    private String activeUser;
    private JTextArea terminalDisplay;

    // --- ANIMATION VARIABLES ---
    private Timer typewriterTimer;
    private int charIndex = 0;
    private String targetText = "";

    // --- SECURITY TIMER VARIABLES ---
    private Timer countdownTimer;
    private JLabel timerLabel;
    private int timeRemaining = 300; // 300 seconds = 5 Minutes
    private AWTEventListener globalTripwire;

    public VaultPanel(SentinelFrame mainMonitor, String username) {
        this.mainMonitor = mainMonitor;
        this.activeUser = username;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. THE HEADER (NORTH) ---
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("SECURE VAULT TERMINAL | CLEARANCE: " + activeUser.toUpperCase());
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 255, 0)); // Hacker Green text
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // THE DIGITAL CLOCK
        timerLabel = new JLabel("AUTO-LOCK: 05:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        timerLabel.setForeground(new Color(220, 20, 60)); // Warning Alarm Red
        headerPanel.add(timerLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. THE DATA DISPLAY (CENTER) ---
        terminalDisplay = new JTextArea();
        terminalDisplay.setEditable(false);
        terminalDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalDisplay.setLineWrap(true);
        terminalDisplay.setWrapStyleWord(true);
        terminalDisplay.setBackground(Color.BLACK);
        terminalDisplay.setForeground(new Color(0, 255, 0));

        JScrollPane scrollPane = new JScrollPane(terminalDisplay);
        add(scrollPane, BorderLayout.CENTER);

        refreshVaultData();

        // --- 3. THE CONTROL CONSOLE (SOUTH) ---
        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        JButton writeNoteBtn = new JButton("Write Classified Note");
        JButton deleteNoteBtn = new JButton("Incinerate Note");
        JButton settingsBtn = new JButton("System Settings");
        JButton logoutBtn = new JButton("Seal Vault (Logout)");

        controlPanel.add(writeNoteBtn);
        controlPanel.add(deleteNoteBtn);
        controlPanel.add(settingsBtn);
        controlPanel.add(logoutBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // --- INITIATE SECURITY PROTOCOLS ---
        startSecurityTimer();

        // --- CLICK EVENTS ---
        writeNoteBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Enter Note Title:");
            if (title == null || title.trim().isEmpty()) return;

            String content = JOptionPane.showInputDialog(this, "Enter Classified Content:");
            if (content == null || content.trim().isEmpty()) return;

            boolean success = VaultManager.saveNote(activeUser, title, content);
            if (success) {
                refreshVaultData();
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
                    refreshVaultData();
                } else {
                    JOptionPane.showMessageDialog(this, "Target not found or access denied.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        settingsBtn.addActionListener(e -> {
            disarmSecurityTimer(); // Turn off timer before switching rooms
            mainMonitor.switchPanel(new SettingsPanel(mainMonitor, activeUser));
        });

        logoutBtn.addActionListener(e -> {
            executeManualLogout();
        });
    }

    // --- SECURITY TIMER METHODS ---

    private void startSecurityTimer() {
        // 1. Build the Auto-Kill Clock (Ticks down every 1000 milliseconds / 1 second)
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;

            // Format the math into a digital clock display (e.g., 04:59)
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("AUTO-LOCK: %02d:%02d", minutes, seconds));

            // If the clock hits 0, violently kill the session
            if (timeRemaining <= 0) {
                executeAutoKill();
            }
        });
        countdownTimer.start();

        // 2. Build the Global Tripwire (Senses ALL mouse and keyboard movement)
        globalTripwire = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                // If they move the mouse or press a key, reset the clock to 5 minutes!
                timeRemaining = 300;
                timerLabel.setText("AUTO-LOCK: 05:00");
            }
        };

        // Attach the tripwire to the entire Java application window
        Toolkit.getDefaultToolkit().addAWTEventListener(globalTripwire,
                AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    private void disarmSecurityTimer() {
        // CRITICAL: We must destroy the tripwire when we leave, or it causes memory leaks!
        if (countdownTimer != null) countdownTimer.stop();
        if (globalTripwire != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(globalTripwire);
        }
    }

    private void executeAutoKill() {
        disarmSecurityTimer();
        SecurityLogger.logEvent(activeUser, "SESSION_TIMEOUT_FORCED_LOGOUT");
        JOptionPane.showMessageDialog(mainMonitor,
                "SESSION TIMEOUT.\nNo activity detected for 5 minutes. Vault sealed.",
                "Security Protocol", JOptionPane.WARNING_MESSAGE);
        mainMonitor.switchPanel(new LoginPanel(mainMonitor));
    }

    private void executeManualLogout() {
        disarmSecurityTimer();
        SecurityLogger.logEvent(activeUser, "MANUAL_LOGOUT");
        mainMonitor.switchPanel(new LoginPanel(mainMonitor));
    }

    // --- MATRIX TYPEWRITER METHODS ---

    private void refreshVaultData() {
        List<String> notes = VaultManager.readNotesForGUI(activeUser);
        StringBuilder fullText = new StringBuilder();

        if (notes.isEmpty()) {
            fullText.append("SYSTEM ALARM: Vault is completely empty.\nNo classified documents found.");
        } else {
            for (String note : notes) {
                fullText.append(note).append("\n\n");
            }
        }
        animateText(fullText.toString());
    }

    private void animateText(String text) {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }

        terminalDisplay.setText("");
        targetText = text;
        charIndex = 0;

        typewriterTimer = new Timer(15, e -> {
            if (charIndex < targetText.length()) {
                terminalDisplay.append(String.valueOf(targetText.charAt(charIndex)));
                charIndex++;
                terminalDisplay.setCaretPosition(terminalDisplay.getDocument().getLength());
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        typewriterTimer.start();
    }
}