package com.sentinel.security;

import com.sentinel.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class AuthManager {

    // Registering user
    public static boolean registerUser(String username,String plainTextPassword){
        String hashedPassword=PasswordHasher.hash(plainTextPassword);
        String SQL="INSERT INTO users(username,pass_hash) VALUES(?,?)";

        try(Connection conn=DatabaseManager.getConnection();
        PreparedStatement pstmt=conn.prepareStatement(SQL)){
            pstmt.setString(1,username);
            pstmt.setString(2,hashedPassword);
            pstmt.executeUpdate();
            System.out.println("Success:User "+username+" Registered Successfully");
            return true;
        }
        catch(SQLException e){
            System.out.println("Error: Could not register user. Username might already exist.");
            return false;
        }
    }

    //User Log in method

    public static boolean loginUser(String username, String plainTextPassword) {
        String sql = "SELECT pass_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String savedHash = rs.getString("pass_hash");

                    if (PasswordHasher.verify(plainTextPassword, savedHash)) {
                        System.out.println("Success: Welcome back, " + username + "!");
                        return true;
                    }
                }
            }
            System.out.println("Error: Invalid username or password.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error: Database connection failed during login.");
            return false;
        }
    }
}