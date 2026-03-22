/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.mycompany.motorphpayroll_OOP.ValidationUtils;

/**
 *
 * @author Amir
 */
public abstract class Employee implements PayrollCalculations, EmployeePermissions {
    // Basic Info
    protected String employeeId;
   protected String lastName, firstName, birthday, address, phoneNumber;
    
    // Government number and Employment info
    protected String sssNumber, philHealth, tinNumber, pagIbig, status, position, supervisor;
    // Financials
   protected double basicSalary, riceSubsidy, phoneAllowance, clothingAllowance;
    protected double grossSemiMonthlyRate, hourlyRate;

    public Employee(String employeeId, String lastName, String firstName, String birthday,
                    String address, String phoneNumber, String sssNumber, String philHealth,
                    String tinNumber, String pagIbig, String status, String position,
                    String supervisor, double basicSalary, double riceSubsidy,
                    double phoneAllowance, double clothingAllowance,
                    double grossSemiMonthlyRate, double hourlyRate) {
        
        // Validation
        validate(employeeId, lastName, firstName, basicSalary, hourlyRate);
        
        //  Sanitize and Assign Strings
        this.employeeId = sanitize(employeeId);
        this.lastName = sanitize(lastName);
        this.firstName = sanitize(firstName);
        this.birthday = sanitize(birthday);
        this.address = sanitize(address);
        this.phoneNumber = sanitize(phoneNumber);
        this.sssNumber = sanitize(sssNumber);
        this.philHealth = sanitize(philHealth);
        this.tinNumber = sanitize(tinNumber);
        this.pagIbig = sanitize(pagIbig);
        this.status = sanitize(status);
        this.position = sanitize(position);
        this.supervisor = sanitize(supervisor);
        
        // Assign Numerics
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }
    private void validate(String id, String last, String first, double salary, double rate) {
        if (!ValidationUtils.isValidEmployeeId(id)) {
            throw new IllegalArgumentException("Invalid employee ID format: " + id);
        }
        if (last == null || last.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (first == null || first.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (salary < 0 || rate < 0) {
            throw new IllegalArgumentException("Salary values cannot be negative");
        }
    }
    
    /**
     * 
     * @param benefitType
     * @return 
     */
    public double calculateBenefit(String benefitType) {
        return switch (benefitType.toLowerCase()) {
            case "rice" -> getRiceSubsidy();
            case "phone" -> getPhoneAllowance();
            case "clothing" -> getClothingAllowance();
            default -> 0.0;
        };
    }

    /**
     * 
     * @return 
     */
    public double calculateBenefit() {
        return getRiceSubsidy() + getPhoneAllowance() + getClothingAllowance();
    }
    
    @Override
       public abstract double calculateSSS(); 
      
    
    
    @Override
    public double calculateGrossSalary(double hoursWorked) {
      
        double standardHours = 40.0; 
        double regularHours = Math.min(hoursWorked, standardHours);
        double overtimeHours = Math.max(hoursWorked - standardHours, 0);
    
         // In PayrollProcessor: (regularHours * rate) + (overtimeHours * rate * multiplier)
            return (regularHours * getHourlyRate()) + calculateAdjustment(overtimeHours);
    }

    @Override
    public double calculateAdjustment(double overtimeHours) {
    return overtimeHours * getHourlyRate() * 1.25;
    }

    @Override
    public double calculateAdjustment(int totalMinutesLate) {
    return totalMinutesLate * (getHourlyRate() / 60.0);
    }

    @Override
    public abstract double calculatePhilHealth();

    @Override
    public abstract double calculatePagIbig();

    @Override
    public double calculateWithholdingTax(double grossSalary) {
        
    
    double sss = calculateSSS();
    double philHealth = calculatePhilHealth();
    double pagIbig = calculatePagIbig();
    
    // 
    double taxableIncome = grossSalary - (sss + philHealth + pagIbig);
    
    // 
    if (taxableIncome <= 20833) return 0;
    else if (taxableIncome <= 33332) return (taxableIncome - 20833) * 0.2;
    else if (taxableIncome <= 66666) return 2500 + (taxableIncome - 33333) * 0.25;
    else if (taxableIncome <= 166666) return 10833 + (taxableIncome - 66667) * 0.3;
    else if (taxableIncome <= 666666) return 40833.33 + (taxableIncome - 166667) * 0.32;
    else return 200833.33 + (taxableIncome - 666667) * 0.35;
}
    @Override public boolean canViewDatabase() { return false; }
    @Override public boolean canViewAllRecords() { return false; }
    @Override public boolean canAddEmployee() { return false; }
    @Override public boolean canDeleteEmployee() { return false; }
    @Override public boolean canEditBasicInfo() { return false; }
    @Override public boolean canEditFinancials() { return false; }
    @Override public boolean canFileLeave() { return false; }
    @Override public boolean canApproveLeave() { return false; }
    @Override public boolean canAccessSystemTools() {return false;}
    @Override public boolean canComputePayroll() {return false;}
    
    // Getters
    public double getHourlyRate() { return hourlyRate; }
    public String getEmployeeId() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBirthday() { return birthday; }
    public String getPosition() { return position; }
    public String getStatus() { return status; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }

    // Additional getters for GUI components
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilHealth() { return philHealth; }
    public String getTinNumber() { return tinNumber; }
    public String getPagIbig() { return pagIbig; }
    public String getSupervisor() { return supervisor; }
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    
    // Setter methods for updating employee data
    public void setLastName(String lastName) { this.lastName = sanitize(lastName); }
    public void setFirstName(String firstName) { this.firstName = sanitize(firstName); }
    public void setBirthday(String birthday) { this.birthday = sanitize(birthday); }
    public void setAddress(String address) { this.address = sanitize(address); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = sanitize(phoneNumber); }
    public void setSssNumber(String sssNumber) { this.sssNumber = sanitize(sssNumber); }
    public void setPhilHealth(String philHealth) { this.philHealth = sanitize(philHealth); }
    public void setTinNumber(String tinNumber) { this.tinNumber = sanitize(tinNumber); }
    public void setPagIbig(String pagIbig) { this.pagIbig = sanitize(pagIbig); }
    public void setStatus(String status) { this.status = sanitize(status); }
    public void setPosition(String position) { this.position = sanitize(position); }
    public void setSupervisor(String supervisor) { this.supervisor = sanitize(supervisor); }
    
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public void calculateContributions() {
        // walang laman hehe
    }
    
    private String sanitize(String input) {
        if (input == null || input.trim().isEmpty() || input.equalsIgnoreCase("null")) {
            return "N/A";
        }
        return input.trim();
    }
    
    public void displayInfo() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Position: " + position);
        System.out.println("Status: " + status);
        System.out.printf("Hourly Rate: PHP %.2f%n", hourlyRate);
    }

    @Override
    public String toString() {
        return String.format("%s - %s, %s (%s)", employeeId, lastName, firstName, position);
    }
}