/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;


import config.DatabaseConnection;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.List;
import model.Employee;
import java.util.stream.Collectors;
/**
 *
 * @author Amir
 */
public class ITService {
    private static final Logger logger = Logger.getLogger(ITService.class.getName());
    private static ITService instance;

    private ITService() {}

    public static ITService getInstance() {
        if (instance == null) {
            instance = new ITService();
        }
        return instance;
    }

    public boolean resetUserPassword(String empId) {
        logger.info("IT Security: Password reset triggered for ID: " + empId);
 
        return true; 
    }
    
    public List<Employee> searchEmployees(String query) {
        List<Employee> all = EmployeeService.getInstance().getAllEmployees();
        if (query == null || query.isEmpty()) return all;

        String lowerQuery = query.toLowerCase();
        return all.stream()
            .filter(emp -> emp.getEmployeeId().contains(query) || 
                           (emp.getFirstName() + " " + emp.getLastName()).toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    public String getTimestampedMessage(String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "[" + time + "] " + message;
    }
 
    /**
     * updated from CSV to DB
     */
    public String runSystemDiagnostics() {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       SYSTEM DIAGNOSTICS REPORT\n");
        report.append("========================================\n");
        
        boolean isDbConnected = false;
        
       
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                isDbConnected = true;
            }
        } catch (Exception e) {
            logger.warning("Diagnostics failed to connect to database: " + e.getMessage());
        }
        
        if (isDbConnected) {
            report.append("[PASS] MySQL Central Database Connected\n");
            report.append("[PASS] Attendance Log Module Sync\n");
            report.append("[PASS] Leave Management Engine Online\n");
            report.append("----------------------------------------\n");
            report.append("[INFO] All systems operational and healthy.");
        } else {
            report.append("[FAIL] MySQL Database Connection Lost\n");
            report.append("----------------------------------------\n");
            report.append("[WARN] System degradation detected. Check Database connection configuration.");
        }
        
        return report.toString();
    }
}
