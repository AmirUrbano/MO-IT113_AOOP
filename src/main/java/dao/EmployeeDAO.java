/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Employee;
import model.EmployeeStatus;

/**
 *
 * @author Amir
 */
public class EmployeeDAO {
    
    private static final Logger logger = Logger.getLogger(EmployeeDAO.class.getName());

    public List<Employee> load() {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { 

            while (rs.next()) {
                String[] data = new String[19];
                data[0] = rs.getString("employee_id");
                data[1] = rs.getString("last_name");
                data[2] = rs.getString("first_name");
                data[3] = rs.getString("birthday");
                data[4] = rs.getString("address");
                data[5] = rs.getString("phone_number");
                data[6] = rs.getString("sss_number");
                data[7] = rs.getString("philhealth_number");
                data[8] = rs.getString("tin_number");
                data[9] = rs.getString("pagibig_number");
                data[10] = rs.getString("status");
                data[11] = rs.getString("position");
                data[12] = rs.getString("supervisor");
                
                // bypass para sa employeestatus.java 
                data[13] = String.valueOf(rs.getDouble("basic_salary"));
                data[14] = String.valueOf(rs.getDouble("rice_subsidy"));
                data[15] = String.valueOf(rs.getDouble("phone_allowance"));
                data[16] = String.valueOf(rs.getDouble("clothing_allowance"));
                data[17] = String.valueOf(rs.getDouble("gross_semi_monthly_rate"));
                data[18] = String.valueOf(rs.getDouble("hourly_rate"));

                Employee emp = EmployeeStatus.createFromCsv(data); // i didnt change the createFromCsv sa employeestatus
                if (emp != null) {
                    employeeList.add(emp);
                }
            }
            logger.info("Successfully loaded " + employeeList.size() + " employees from MySQL Database.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while loading employees: " + e.getMessage());
        }
        return employeeList;
     }
    
    
    
      public void save(List<Employee> employeeList) {
        if (employeeList == null) {
            logger.warning("Attempted to save a null employee list. Operation cancelled.");
            return;
        }

        String sql = "INSERT INTO employees (employee_id, last_name, first_name, birthday, address, phone_number, "
                   + "sss_number, philhealth_number, tin_number, pagibig_number, status, position, supervisor, "
                   + "basic_salary, rice_subsidy, phone_allowance, clothing_allowance, gross_semi_monthly_rate, hourly_rate) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE "
                   + "last_name=?, first_name=?, birthday=?, address=?, phone_number=?, sss_number=?, "
                   + "philhealth_number=?, tin_number=?, pagibig_number=?, status=?, position=?, supervisor=?, "
                   + "basic_salary=?, rice_subsidy=?, phone_allowance=?, clothing_allowance=?, gross_semi_monthly_rate=?, hourly_rate=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); 

            for (Employee emp : employeeList) {
                //  insert part
                pstmt.setString(1, emp.getEmployeeId());
                pstmt.setString(2, emp.getLastName());
                pstmt.setString(3, emp.getFirstName());
                pstmt.setString(4, emp.getBirthday());
                pstmt.setString(5, emp.getAddress());
                pstmt.setString(6, emp.getPhoneNumber());
                pstmt.setString(7, emp.getSssNumber());
                pstmt.setString(8, emp.getPhilHealth());
                pstmt.setString(9, emp.getTinNumber());
                pstmt.setString(10, emp.getPagIbig());
                pstmt.setString(11, emp.getStatus());
                pstmt.setString(12, emp.getPosition());
                pstmt.setString(13, emp.getSupervisor());
                pstmt.setDouble(14, emp.getBasicSalary());
                pstmt.setDouble(15, emp.getRiceSubsidy());
                pstmt.setDouble(16, emp.getPhoneAllowance());
                pstmt.setDouble(17, emp.getClothingAllowance());
                pstmt.setDouble(18, emp.getGrossSemiMonthlyRate());
                pstmt.setDouble(19, emp.getHourlyRate());

                // update part
                pstmt.setString(20, emp.getLastName());
                pstmt.setString(21, emp.getFirstName());
                pstmt.setString(22, emp.getBirthday());
                pstmt.setString(23, emp.getAddress());
                pstmt.setString(24, emp.getPhoneNumber());
                pstmt.setString(25, emp.getSssNumber());
                pstmt.setString(26, emp.getPhilHealth());
                pstmt.setString(27, emp.getTinNumber());
                pstmt.setString(28, emp.getPagIbig());
                pstmt.setString(29, emp.getStatus());
                pstmt.setString(30, emp.getPosition());
                pstmt.setString(31, emp.getSupervisor());
                pstmt.setDouble(32, emp.getBasicSalary());
                pstmt.setDouble(33, emp.getRiceSubsidy());
                pstmt.setDouble(34, emp.getPhoneAllowance());
                pstmt.setDouble(35, emp.getClothingAllowance());
                pstmt.setDouble(36, emp.getGrossSemiMonthlyRate());
                pstmt.setDouble(37, emp.getHourlyRate());

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit(); 
            logger.info("Successfully synced employee list to MySQL Database.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while saving employees: " + e.getMessage());
        }
    }
    
      public boolean deleteFromDatabase(String employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while deleting employee: " + e.getMessage());
            return false;
        }
    }
 
}

