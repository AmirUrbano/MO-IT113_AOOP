/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AttendanceRecord;

/**
 *
 * @author Amir
 */
public class AttendanceDAO {
    private static final Logger logger = Logger.getLogger(AttendanceDAO.class.getName());
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    
    public List<AttendanceRecord> load() {
    List<AttendanceRecord> records = new ArrayList<>();
    String sql = "SELECT employee_id, log_date, time_in, time_out FROM attendance_logs";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
                
        while (rs.next()) {
            String employeeId = rs.getString("employee_id");
            String cleanDate = rs.getDate("log_date").toString(); // yyyy-MM-dd
            
            java.sql.Time sqlTimeIn = rs.getTime("time_in");
            java.sql.Time sqlTimeOut = rs.getTime("time_out");
                
     
            String timeInStr = (sqlTimeIn != null) ? sqlTimeIn.toLocalTime().format(TIME_FORMATTER) : "00:00";
            String timeOutStr = (sqlTimeOut != null) ? sqlTimeOut.toLocalTime().format(TIME_FORMATTER) : "00:00";
            records.add(new AttendanceRecord(employeeId, cleanDate, timeInStr, timeOutStr));
        }      
        logger.info("Successfully loaded " + records.size() + " attendance records from Database.");
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Database error while loading attendance: " + e.getMessage());
    }
    return records;
}
                
   public void save(List<AttendanceRecord> records) {
        if (records == null) {
            logger.warning("Attempted to save a null attendance list. Operation cancelled.");
            return;
        }

        String sql = "INSERT INTO attendance_logs (employee_id, log_date, time_in, time_out) "
                   + "VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE time_out = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); 

            for (AttendanceRecord record : records) {
                pstmt.setString(1, record.getEmployeeId());
                pstmt.setString(2, record.getDate()); // yyyy-MM-dd
                
                
                pstmt.setString(3, record.getLogInTime().format(TIME_FORMATTER));
                pstmt.setString(4, record.getLogOutTime().format(TIME_FORMATTER));
                
                pstmt.setString(5, record.getLogOutTime().format(TIME_FORMATTER));

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit(); 
            logger.info("Successfully synced attendance list to MySQL Database (attendance_logs).");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while saving attendance: " + e.getMessage());
        }
    }
}
