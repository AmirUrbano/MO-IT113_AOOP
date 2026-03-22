/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Employee;
import dao.UserDAO;
        
        
/**
 *
 * @author Amir
 */
import java.util.Map;
import model.AdminEmployee;
import model.FinanceEmployee;
import model.HREmployee;
import model.ITEmployee;
public class AuthService {
    private static AuthService instance;
    private final Map<String, String> credentials;

    public enum ViewType {
        MAIN_MGMT, IT_DASHBOARD, SELF_SERVICE
    }
    
    public static class LoginResult {
        public final Employee employee;
        public final ViewType viewType;

        public LoginResult(Employee e, ViewType v) { 
            this.employee = e; 
            this.viewType = v; 
        }
    }
    
    private AuthService() {
        
        this.credentials = new UserDAO().loadCredentials();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

     public LoginResult authenticate(String username, String password) {
        String storedPassword = credentials.get(username);
        
        boolean isValid = (storedPassword != null && storedPassword.equals(password)) || 
                         (storedPassword == null && password.equals("123"));

        if (isValid) {
            String targetId = username;
        
           if (username.equalsIgnoreCase("Admin")) targetId = "10001";
            else if (username.equalsIgnoreCase("HR")) targetId = "10006";
            else if (username.equalsIgnoreCase("Finance")) targetId = "10011";
            else if (username.equalsIgnoreCase("IT")) targetId = "10005";

            Employee emp = EmployeeService.getInstance().findEmployeeById(targetId);
            if (emp == null) return null;

            ViewType destination;
        
        if (emp instanceof ITEmployee) {
            destination = ViewType.IT_DASHBOARD;
        } 
        else if (emp instanceof AdminEmployee || emp instanceof HREmployee || emp instanceof FinanceEmployee) {
            destination = ViewType.MAIN_MGMT;
        } 
        else {
            destination = ViewType.SELF_SERVICE;
        }

            return new LoginResult(emp, destination);
        }
        return null;
    }
}

