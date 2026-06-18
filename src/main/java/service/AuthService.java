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
public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO;

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
        this.userDAO = new UserDAO();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

     public LoginResult authenticate(String username, String password) {
        
        String targetId = userDAO.validateLogin(username, password);
        
        if (targetId == null) {
            return null;
        }

        boolean isRoleLogin = username.equalsIgnoreCase("Admin") || 
                             username.equalsIgnoreCase("HR") || 
                             username.equalsIgnoreCase("Finance");
                             
        boolean isItRoleLogin = username.equalsIgnoreCase("it");

        ViewType destination;
        if (isItRoleLogin) {
            destination = ViewType.IT_DASHBOARD;
        } else if (isRoleLogin) {
            destination = ViewType.MAIN_MGMT;
        } else {
            destination = ViewType.SELF_SERVICE;
        }

        Employee emp = EmployeeService.getInstance().findEmployeeById(targetId);
        if (emp == null) {
            return null;
        }

        return new LoginResult(emp, destination);
    }
}

