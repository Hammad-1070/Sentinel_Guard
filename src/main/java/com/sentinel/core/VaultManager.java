package com.sentinel.core;

import com.sentinel.db.DatabaseManager;
import com.sentinel.security.CryptoManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VaultManager {


    private static int getUserId(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user ID.");
        }
        return -1;
    }


    private static String getUnlockedPersonalKey(String username) {
        String sql = "SELECT encrypted_master_key FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lockedEnvelope = rs.getString("encrypted_master_key");

                    return CryptoManager.decrypt(lockedEnvelope, CryptoManager.SYSTEM_KEY);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error unlocking personal key.");
        }
        return null;
    }


    public static void saveNote(String username, String title, String plainTextContent) {
        int userId = getUserId(username);
        String personalKey = getUnlockedPersonalKey(username);

        if (userId == -1 || personalKey == null) {
            System.out.println("Error: Security clearance denied. Cannot access vault.");
            return;
        }


        String encryptedContent = CryptoManager.encrypt(plainTextContent, personalKey);

        String sql = "INSERT INTO vault_notes (user_id, note_title, encrypted_content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, encryptedContent);
            pstmt.executeUpdate();

            System.out.println("Success: Classified note '" + title + "' securely vaulted.");
        } catch (SQLException e) {
            System.out.println("Error: Failed to write to vault.");
        }
    }


    public static void readNotes(String username) {
        int userId = getUserId(username);
        if (userId == -1) {
            System.out.println("Critical Error: Database could not locate your User ID.");
            return;
        }

        String personalKey = getUnlockedPersonalKey(username);
        if (personalKey == null) {
            System.out.println("Critical Error: Failed to unlock your Personal Master Key. Your encryption envelope might be corrupted.");
            return;
        }


        String sql = "SELECT note_id, note_title, encrypted_content, created_at FROM vault_notes WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {

                System.out.println("\n=== YOUR CLASSIFIED VAULT ===");
                boolean hasNotes = false;

                while (rs.next()) {
                    hasNotes = true;
                    int noteId = rs.getInt("note_id");
                    String title = rs.getString("note_title");
                    String encryptedContent = rs.getString("encrypted_content");
                    String date = rs.getString("created_at");

                    // Decrypt the content live in memory
                    String decryptedContent = CryptoManager.decrypt(encryptedContent, personalKey);

                    System.out.println("\n[Note ID: " + noteId + "] Title: " + title + " | Date: " + date);
                    System.out.println("Content: " + decryptedContent);
                    System.out.println("-----------------------------------");
                }

                if (!hasNotes) {
                    System.out.println("Your vault is currently empty. No notes found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: Failed to open vault.");
            System.out.println("MySQL says: " + e.getMessage());
        }
    }

    public static void deleteNote(String username, int targetNoteId) {
        int userId = getUserId(username);

        if (userId == -1) {
            System.out.println("Error: Security clearance denied.");
            return;
        }


        String sql = "DELETE FROM vault_notes WHERE note_id = ? AND user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, targetNoteId);
            pstmt.setInt(2, userId);


            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("System: Note #" + targetNoteId + " has been permanently incinerated.");
            } else {

                System.out.println("Error: Note not found, or you do not have permission to delete it.");
            }

        } catch (SQLException e) {
            System.out.println("Error: Failed to incinerate note.");
        }
    }
}