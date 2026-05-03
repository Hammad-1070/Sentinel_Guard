package com.sentinel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager{

    private static final String URL = "jdbc:mysql://localhost:3306/sentinel_guard_DB";

    private static final String USER="root";
    private static final String PASSOWRD="1234";


    public static Connection getConnection() throws SQLException{

        return DriverManager.getConnection(URL,USER,PASSOWRD);
    }
}