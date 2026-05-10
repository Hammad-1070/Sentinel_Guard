package com.sentinel.security;

import com.sentinel.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthManager {

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
}
