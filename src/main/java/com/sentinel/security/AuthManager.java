package com.sentinel.security;

import com.sentinel.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class AuthManager {

    // Registering user

    public static boolean registerUser(String username, String plainTextPassword, String securityQuestion, String securityAnswer) {

        String hashedPassword = PasswordHasher.hash(plainTextPassword);

        String hashedAnswer = PasswordHasher.hash(securityAnswer.trim().toLowerCase());

        String personalMasterKey = CryptoManager.generateMasterKey();

        String lockedKeyEnvelope = CryptoManager.encrypt(personalMasterKey, CryptoManager.SYSTEM_KEY);

        String sql = "INSERT INTO users (username, pass_hash, security_question_1, security_answer_hash_1, encrypted_master_key) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, securityQuestion);
            pstmt.setString(4, hashedAnswer);


            pstmt.setString(5, lockedKeyEnvelope);

            pstmt.executeUpdate();
            System.out.println("Success: User " + username + " registered. Personal Master Key securely vaulted.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error: Could not register user. Username might already exist.");
            return false;
        }
    }

    public static String getSecurityQuestion(String username) {
        String sql = "SELECT security_question_1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("security_question_1");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database.");
        }
        return null; // Return nothing if the user doesn't exist
    }


    public static boolean loginUser(String username, String plainTextPassword, String typedAnswer) {
        String sql = "SELECT pass_hash, security_answer_hash_1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String savedPassHash = rs.getString("pass_hash");
                    String savedAnswerHash = rs.getString("security_answer_hash_1");

                    if (PasswordHasher.verify(plainTextPassword, savedPassHash)) {


                        String normalizedAnswer = typedAnswer.trim().toLowerCase();
                        if (PasswordHasher.verify(normalizedAnswer, savedAnswerHash)) {
                            SecurityLogger.logEvent(username, "LOGIN_SUCCESS");
                            String updateTimeSql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
                            try (PreparedStatement timeStmt = conn.prepareStatement(updateTimeSql)) {
                                timeStmt.setString(1, username);
                                timeStmt.executeUpdate();
                            }
                            System.out.println("Success: Welcome back, " + username + "! Identity fully verified.");
                            return true;
                        } else {
                            System.out.println("Error: Security answer incorrect.");
                            return false;
                        }
                    }
                }
            }
            SecurityLogger.logEvent(username, "LOGIN_FAILED");
            System.out.println("Error: Invalid username or password.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error: Database connection failed.");
            return false;
        }
    }

    public static boolean deleteUser(String username, String plainTextPassword) {
        // First, we run a mini-login check to ensure they know the password
        String verifySql = "SELECT pass_hash FROM users WHERE username = ?";
        String deleteSql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {

            verifyStmt.setString(1, username);
            try (ResultSet rs = verifyStmt.executeQuery()) {
                if (rs.next()) {
                    String savedHash = rs.getString("pass_hash");

                    // If the password is correct, we execute the deletion
                    if (PasswordHasher.verify(plainTextPassword, savedHash)) {

                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setString(1, username);
                            deleteStmt.executeUpdate();
                            System.out.println("System: Account for '" + username + "' has been permanently erased.");
                            return true;
                        }
                    }
                }
            }
            System.out.println("Error: Incorrect password. Deletion aborted.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error: Could not connect to database.");
            return false;
        }
    }
}