package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sentinel.gui.SentinelFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.out.println("Error initializing Dark Theme.");
        }

        SwingUtilities.invokeLater(() -> {
            SentinelFrame mainMonitor = new SentinelFrame();

            mainMonitor.setVisible(true);
        });
    }
}