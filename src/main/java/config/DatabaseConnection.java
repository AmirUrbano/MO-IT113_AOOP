/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Amir
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/motorph";
    private static final String USER = "root";
    private static final String PASSWORD = "test123";
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Databased Connected Success");
            } 
        } catch (ClassNotFoundException e) {
            System.err.print("Database not found " + e.getMessage());
        }
        return connection;
    }
    
    
}
