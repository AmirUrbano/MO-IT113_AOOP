/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import config.DatabaseConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Amir
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public String validateLogin(String username, String password) {
        String sql = "SELECT employee_id FROM user_credentials WHERE username = ? AND password = ?";
        String hashedInputPassword = hashSHA256(password); // hashed password

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedInputPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("employee_id"); // success login
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during authentication check: " + e.getMessage(), e);
        }
        return null; // failed
    }
    
    public void createAccount(String employeeId, String username, String password) {
        String sql = "INSERT INTO user_credentials (employee_id, username, password) VALUES (?, ?, ?)";
        String hashedPassword = hashSHA256(password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);

            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "New database account successfully created for username: {0}", username);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while writing new user account: " + e.getMessage(), e);
        }
    }
   
   public String hashSHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, "Critical Security Error: SHA-256 algorithm not found.", ex);
            return base; 
        }
    } 
}

