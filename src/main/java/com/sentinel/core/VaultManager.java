package com.sentinel.core;

import com.sentinel.db.DatabaseManager;
import com.sentinel.security.CryptoManager;
import java.util.ArrayList;
import java.util.List;
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


    public static boolean saveNote(String username, String title, String plainTextContent) {
        int userId = getUserId(username);
        String personalKey = getUnlockedPersonalKey(username);

        if (userId == -1 || personalKey == null) return false;

        String encryptedContent = CryptoManager.encrypt(plainTextContent, personalKey);
        String sql = "INSERT INTO vault_notes (user_id, note_title, encrypted_content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, encryptedContent);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<String> readNotesForGUI(String username) {
        List<String> notesList = new ArrayList<>();
        int userId = getUserId(username);
        String personalKey = getUnlockedPersonalKey(username);

        if (userId == -1 || personalKey == null) {
            notesList.add("Critical Error: Security clearance invalid.");
            return notesList;
        }

        String sql = "SELECT note_id, note_title, encrypted_content, created_at FROM vault_notes WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int noteId = rs.getInt("note_id");
                    String title = rs.getString("note_title");
                    String encrypted = rs.getString("encrypted_content");
                    String date = rs.getString("created_at");

                    String decrypted = CryptoManager.decrypt(encrypted, personalKey);

                    // Format the note for the UI screen
                    String formattedNote = String.format("[ID: %d] %s (Logged: %s)\nContent: %s\n--------------------------------------------------",
                            noteId, title, date, decrypted);
                    notesList.add(formattedNote);
                }
            }
        } catch (SQLException e) {
            notesList.add("Error connecting to the vault database.");
        }
        return notesList;
    }

    public static boolean deleteNote(String username, int targetNoteId) {
        int userId = getUserId(username);
        if (userId == -1) return false;

        String sql = "DELETE FROM vault_notes WHERE note_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, targetNoteId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            return false;
        }
    }
}