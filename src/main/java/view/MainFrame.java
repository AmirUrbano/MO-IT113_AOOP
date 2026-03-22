

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package view;

import service.EmployeeService;
import model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import model.EmployeeStatus;
import model.PayrollResult;
import service.PayrollService;

/**
 *
 * @author Amir
 */
public class MainFrame extends JFrame {
    
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private EmployeeService employeeDetails;
    
    // UI Buttons from your progress
    private JButton viewEmployeeBtn, newEmployeeBtn, updateEmployeeBtn, deleteEmployeeBtn;
    private JButton refreshBtn, logoutBtn, manageLeavesBtn;
    
  
    private EmployeeFormPanel detailPanel;
    
    private Employee currentUser;
    private Employee selectedEmployee = null;
    private EmployeeDetailFrame detailFrame = null;
    private AddEmployeeFrame addEmployeeFrame = null;
    private LeaveManagementFrame leaveManagementFrame = null;

    public MainFrame(Employee user) {
        this.currentUser = user;
        this.employeeDetails = EmployeeService.getInstance();
        initializeGUI();
        applySecuritySettings();
        loadEmployeeData();
        
        
        setTitle("MotorPH Management System - Welcome, " + currentUser.getFirstName());
        
   
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("Employee data successfully saved on shutdown.");
                } catch (Exception ex) {
                    System.err.println("Error saving employee data: " + ex.getMessage());
                }
                }
            });
        }

    private void initializeGUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    getContentPane().setBackground(new Color(245, 245, 245));

  
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(Color.WHITE);
    headerPanel.setPreferredSize(new Dimension(1400, 65));
    headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

    JLabel lblLogo = new JLabel("  MOTORPH MANAGEMENT SYSTEM");
    lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lblLogo.setForeground(new Color(44, 62, 80));

    JLabel lblUser = new JLabel("Logged in as: " + currentUser.getFirstName() + " " + currentUser.getLastName() + "  ");
    lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    headerPanel.add(lblLogo, BorderLayout.WEST);
    headerPanel.add(lblUser, BorderLayout.EAST);
    add(headerPanel, BorderLayout.NORTH);

   
    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(new Color(44, 62, 80));
    sidebar.setPreferredSize(new Dimension(240, 800));

    sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
    
   sidebar.add(createNavButton("Employee Records", e -> {
    employeeTable.clearSelection(); // Deselect any selected employee
    loadEmployeeData();            // Refresh the list from the service
    detailPanel.clearForm();        // Clear the right-side preview panel
    JOptionPane.showMessageDialog(this, "Employee Records Refreshed.");
    }));
    
    // Finance Features
    if (currentUser.canEditFinancials()) {
        sidebar.add(createNavButton("Payroll Center", e -> {
        String selectedId = JOptionPane.showInputDialog(this, "Enter Employee ID:", "Generate Payslip", JOptionPane.QUESTION_MESSAGE);
    
    if (selectedId != null && !selectedId.trim().isEmpty()) {
       
        String[] months = {"1 - January", "2 - February", "3 - March", "4 - April", "5 - May", "6 - June","7 - July","8 - August", "9 - September", "10 - October", "11 - November", "12 - December"};
        String selectedMonthStr = (String) JOptionPane.showInputDialog(this, 
                "Select Payroll Month:", "Period Selection", 
                JOptionPane.QUESTION_MESSAGE, null, months, months[2]); // Default to March

        if (selectedMonthStr != null) {
            try {
            
                int month = Integer.parseInt(selectedMonthStr.split(" - ")[0]);

                PayrollResult result = PayrollService.getInstance().getPayrollResultForGUI(selectedId, month);

                if (result != null) {
                    PayslipFrame payslip = new PayslipFrame(result);
                    payslip.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating payroll: " + ex.getMessage());
            }
        }
    }
}));
    }
    
    manageLeavesBtn = createNavButton("Manage Leaves", new ManageLeavesListener());
    sidebar.add(manageLeavesBtn);
    
    sidebar.add(Box.createVerticalGlue()); // Push Logout to bottom
    
    logoutBtn = createNavButton("Logout System", new LogoutEmployeeListener());
    sidebar.add(logoutBtn);
    sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
    
    add(sidebar, BorderLayout.WEST);

   
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setDividerLocation(850);
    splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    splitPane.setLeftComponent(createTablePanel());
    
    detailPanel = new EmployeeFormPanel();
    detailPanel.setFieldsEditable(false, false);
    splitPane.setRightComponent(detailPanel);
    
    add(splitPane, BorderLayout.CENTER);

  
    add(createBottomActionBar(), BorderLayout.SOUTH);

    setSize(1400, 850);
    setLocationRelativeTo(null);
}

    


  private JPanel createBottomActionBar() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

 
    newEmployeeBtn = new JButton("NEW EMPLOYEE");
    newEmployeeBtn.setBackground(new Color(40, 167, 69));
    newEmployeeBtn.setForeground(Color.WHITE);
    newEmployeeBtn.addActionListener(new NewEmployeeListener());

    updateEmployeeBtn = new JButton("SAVE UPDATES");
    updateEmployeeBtn.setBackground(new Color(255, 193, 7));
    updateEmployeeBtn.addActionListener(new UpdateEmployeeListener());
    updateEmployeeBtn.setEnabled(false);

    deleteEmployeeBtn = new JButton("DELETE");
    deleteEmployeeBtn.setBackground(new Color(220, 53, 69));
    deleteEmployeeBtn.setForeground(Color.WHITE);
    deleteEmployeeBtn.addActionListener(new DeleteEmployeeListener());
    deleteEmployeeBtn.setEnabled(false);

    viewEmployeeBtn = new JButton("VIEW FULL DETAILS");
    viewEmployeeBtn.addActionListener(new ViewEmployeeListener());

    refreshBtn = new JButton("REFRESH");
    refreshBtn.addActionListener(e -> loadEmployeeData());

    
    panel.add(newEmployeeBtn);
    panel.add(updateEmployeeBtn);
    panel.add(deleteEmployeeBtn);
    panel.add(new JSeparator(JSeparator.VERTICAL));
    panel.add(viewEmployeeBtn);
    panel.add(refreshBtn);

    return panel;
}
  
   private JButton createNavButton(String text, ActionListener action) {
        JButton btn = new JButton("   " + text);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(236, 240, 241));
        btn.setBackground(new Color(44, 62, 80));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (action != null) btn.addActionListener(action);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(52, 73, 94)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(44, 62, 80)); }
        });
        return btn;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Employee Records"));

        String[] columnNames = {
            "Employee #", "Last Name", "First Name", "SSS Number", 
            "PhilHealth Number", "TIN", "Pag-IBIG Number"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(30); 
        employeeTable.getTableHeader().setReorderingAllowed(false);

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });

        tablePanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        return tablePanel;
    }
    
    private void populateFormFromSelectedRow() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            selectedEmployee = null;
            detailPanel.clearForm();
            detailPanel.setFieldsEditable(currentUser.canEditBasicInfo(), currentUser.canEditFinancials());
            updateEmployeeBtn.setEnabled(false);
            deleteEmployeeBtn.setEnabled(false);
            return;
        }

        String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
        selectedEmployee = employeeDetails.findEmployeeById(employeeId);
        
        if (selectedEmployee != null) {
            detailPanel.setEmployeeData(selectedEmployee);
            
            detailPanel.setFieldsEditable(currentUser.canEditBasicInfo(), currentUser.canEditFinancials());
            
            updateEmployeeBtn.setEnabled(currentUser.canEditBasicInfo() || currentUser.canEditFinancials());
            deleteEmployeeBtn.setEnabled(currentUser.canDeleteEmployee());
            viewEmployeeBtn.setEnabled(true);
        }
    }


    private void loadEmployeeData() {
        tableModel.setRowCount(0); 
        boolean canViewAll = currentUser.canViewAllRecords(); 
        
        List<Employee> employees = employeeDetails.getAllEmployees();
        for (Employee emp : employees) {
            if (!canViewAll && !emp.getEmployeeId().equals(currentUser.getEmployeeId())) {
            continue; 
        }

        Object[] rowData = {
            emp.getEmployeeId(), emp.getLastName(), emp.getFirstName(),
            emp.getSssNumber(), emp.getPhilHealth(), emp.getTinNumber(), emp.getPagIbig()
        };
        tableModel.addRow(rowData);
    }
  }
    
    private void applySecuritySettings() {
   
    boolean canView = currentUser.canViewDatabase();      // view all database
    boolean canAdd = currentUser.canAddEmployee();        // The "New" Button
    boolean canUpdate = currentUser.canEditBasicInfo() || currentUser.canEditFinancials(); // The "Update" Button
    boolean canDelete = currentUser.canDeleteEmployee();  // The "Delete" Button
    boolean canApprove = currentUser.canApproveLeave();   // Admin-only Leave actions
    
    newEmployeeBtn.setVisible(canAdd);
    deleteEmployeeBtn.setVisible(canDelete);
    updateEmployeeBtn.setVisible(canUpdate);
    manageLeavesBtn.setVisible(canApprove);
    
    if (!canView) {
        JOptionPane.showMessageDialog(this, "Access Denied: You do not have permission to view the employee database.");
        this.dispose();
        new LoginFrame().setVisible(true);
    }
} 
   
      public void refreshTable() {
        loadEmployeeData();
 }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "MotoPH Employee Management System\n" +
            "Version 2.0\n" +
            "Employee record management with update and delete functionality",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }

    
    
    
    // Action Listeners
    private class UpdateEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedEmployee == null) return;

            try {
              
                String[] data = detailPanel.validateAndGetFormData();
                Employee updatedEmployee = EmployeeStatus.createFromCsv(data);

                if (updatedEmployee != null && employeeDetails.updateEmployee(updatedEmployee)) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Employee updated successfully!");
                    loadEmployeeData();
                    selectedEmployee = updatedEmployee;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Update Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteEmployeeListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(MainFrame.this, "Please select an employee first.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            MainFrame.this, 
            "Are you sure you want to delete " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + "?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                if (employeeDetails.deleteEmployee(selectedEmployee.getEmployeeId())) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Employee deleted successfully.");
                    loadEmployeeData(); 
                    detailPanel.clearForm(); 
                    selectedEmployee = null; 
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Delete failed: Employee ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                    MainFrame.this, 
                    ex.getMessage(), 
                    "Security Access Denied", 
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

    private class ViewEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedEmployee != null) {
                if (detailFrame == null || !detailFrame.isDisplayable()) {
                    detailFrame = new EmployeeDetailFrame(selectedEmployee, currentUser, true);
                    detailFrame.setVisible(true);
                } else {
                    detailFrame.toFront();
                    detailFrame.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Please select an employee from the table first.", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private class NewEmployeeListener implements ActionListener {
        @Override 
        public void actionPerformed(ActionEvent e) { 
            if (addEmployeeFrame == null || !addEmployeeFrame.isDisplayable()) {
                addEmployeeFrame = new AddEmployeeFrame(MainFrame.this, employeeDetails);
                addEmployeeFrame.setVisible(true);
            } else {
                addEmployeeFrame.toFront();
                addEmployeeFrame.requestFocus();
            }
        }
    }
    
    private class ManageLeavesListener implements ActionListener {
        @Override 
        public void actionPerformed(ActionEvent e) { 
            if (leaveManagementFrame == null || !leaveManagementFrame.isDisplayable()) {
                leaveManagementFrame = new LeaveManagementFrame();
                leaveManagementFrame.setVisible(true);
            } else {
                leaveManagementFrame.toFront();
                leaveManagementFrame.requestFocus();
            }
        }
    }

    private class LogoutEmployeeListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            if (JOptionPane.showConfirmDialog(MainFrame.this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                MainFrame.this.dispose();
                if (detailFrame != null) detailFrame.dispose();
                if (addEmployeeFrame != null) addEmployeeFrame.dispose();
                if (leaveManagementFrame != null) leaveManagementFrame.dispose();
                
                new LoginFrame().setVisible(true);
            }
        }
    }
}    