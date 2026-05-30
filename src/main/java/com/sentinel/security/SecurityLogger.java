package com.sentinel.security;

import com.sentinel.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityLogger {


    public static void logEvent(String username, String actionType) {
        String sql = "INSERT INTO audit_logs (attempted_username, action_type) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, actionType);
            pstmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println("[WARNING: Security Logger Offline]");
        }
    }

   
    public static void printRecentLogs() {
        String sql = "SELECT attempted_username, action_type, log_time FROM audit_logs ORDER BY log_time DESC LIMIT 10";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n=== RECENT SECURITY LOGS ===");
            while (rs.next()) {
                String user = rs.getString("attempted_username");
                String action = rs.getString("action_type");
                String time = rs.getString("log_time");

                System.out.println("[" + time + "] USER: " + user + " | ACTION: " + action);
            }
            System.out.println("============================");

        } catch (SQLException e) {
            System.out.println("Error reading audit logs.");
        }
    }
}