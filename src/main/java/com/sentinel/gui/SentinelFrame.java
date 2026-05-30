package com.sentinel.gui;

import javax.swing.*;

public class SentinelFrame extends JFrame {

    public SentinelFrame() {
        setTitle("Sentinel Guard - Secure Vault");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Boot up the system directly to the Login Panel
        switchPanel(new LoginPanel(this));
    }

    /**
     * THE CHANNEL SWITCHER: Replaces the current screen with a new one.
     */
    public void switchPanel(JPanel newPanel) {
        // Remove whatever is currently on the screen
        getContentPane().removeAll();

        // Add the new screen
        getContentPane().add(newPanel);

        // Force the monitor to instantly redraw the pixels
        revalidate();
        repaint();
    }
}