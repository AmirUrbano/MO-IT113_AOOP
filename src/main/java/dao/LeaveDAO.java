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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.LeaveRequest;
import model.Employee;
import service.EmployeeService;
/**
 *
 * @author Amir
 */
 public class LeaveDAO {
    private static final Logger logger = Logger.getLogger(LeaveDAO.class.getName());
    
    public List<LeaveRequest> load () {
        
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT request_id, employee_id, leave_type, start_date, end_date, status FROM leave_requests";
        
        EmployeeService empService = EmployeeService.getInstance();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { 

            while (rs.next()) {
                String requestId = rs.getString("request_id");
                String employeeId = rs.getString("employee_id");
                String leaveType = rs.getString("leave_type");
                String startDate = rs.getDate("start_date").toString(); 
                String endDate = rs.getDate("end_date").toString();     
                String status = rs.getString("status");

                // for name of employee
                Employee emp = empService.findEmployeeById(employeeId);
                String fullName = (emp != null) ? (emp.getFirstName() + " " + emp.getLastName()) : "Unknown Employee";

                // constructor model based on LeaveRequest model
                LeaveRequest request = new LeaveRequest(requestId, employeeId, fullName, startDate, endDate, leaveType, status);
                list.add(request);
            }
            logger.info("Successfully loaded " + list.size() + " leave requests from MySQL Database.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while loading leave requests: " + e.getMessage());
        }
        return list;
    }
    
   public void save(List<LeaveRequest> list) {
        if (list == null) {
            logger.warning("Attempted to save a null leave list. Operation cancelled.");
            return;
        }

        String sql = "INSERT INTO leave_requests (request_id, employee_id, leave_type, start_date, end_date, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); 

            for (LeaveRequest lr : list) {
                pstmt.setString(1, lr.getRequestId());
                pstmt.setString(2, lr.getEmployeeId());
                pstmt.setString(3, lr.getLeaveType());
                pstmt.setString(4, lr.getStartDate()); // yyyy-MM-dd
                pstmt.setString(5, lr.getEndDate());   // yyyy-MM-dd
                pstmt.setString(6, lr.getStatus());
                
                pstmt.setString(7, lr.getStatus());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            logger.info("Successfully synced leave requests to MySQL Database.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while saving leave requests: " + e.getMessage());
        }
    }
}
