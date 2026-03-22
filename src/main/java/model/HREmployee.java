/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Amir
 */
public class HREmployee  extends Employee {

    public HREmployee(String employeeId, String lastName, String firstName, String birthday, String address, String phoneNumber, String sssNumber, String philHealth, String tinNumber, String pagIbig, String status, String position, String supervisor, double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {
        super(employeeId, lastName, firstName, birthday, address, phoneNumber, sssNumber, philHealth, tinNumber, pagIbig, status, position, supervisor, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
    }
    @Override public boolean canViewDatabase() { return true; }
    @Override public boolean canViewAllRecords() { return true; }
    @Override public boolean canAddEmployee() { return true; }
    @Override public boolean canDeleteEmployee() { return true; }
    @Override public boolean canEditBasicInfo() { return true; } 
    @Override public boolean canApproveLeave() { return true; }  
    
      @Override
       public double calculateSSS() {
        double salary = getBasicSalary();
        if (salary <= 3250) return 135.00;
        if (salary <= 3750) return 157.50;
        if (salary <= 4250) return 180.00;
        if (salary <= 4750) return 202.50;
        if (salary <= 5250) return 225.00;
        if (salary <= 5750) return 247.50;
        if (salary <= 6250) return 270.00;
        if (salary <= 6750) return 292.50;
        if (salary <= 7250) return 315.00;
        if (salary <= 7750) return 337.50;
        if (salary <= 8250) return 360.00;
        if (salary <= 8750) return 382.50;
        if (salary <= 9250) return 405.00;
        if (salary <= 9750) return 427.50;
        if (salary <= 10250) return 450.00;
        if (salary <= 10750) return 472.50;
        if (salary <= 11250) return 495.00;
        if (salary <= 11750) return 517.50;
        if (salary <= 12250) return 540.00;
        if (salary <= 12750) return 562.50;
        if (salary <= 13250) return 585.00;
        if (salary <= 13750) return 607.50;
        if (salary <= 14250) return 630.00;
        if (salary <= 14750) return 652.50;
        if (salary <= 15250) return 675.00;
        if (salary <= 15750) return 697.50;
        if (salary <= 16250) return 720.00;
        if (salary <= 16750) return 742.50;
        if (salary <= 17250) return 765.00;
        if (salary <= 17750) return 787.50;
        if (salary <= 18250) return 810.00;
        if (salary <= 18750) return 832.50;
        if (salary <= 19250) return 855.00;
        if (salary <= 19750) return 877.50;
        if (salary <= 20250) return 900.00;
        if (salary <= 20750) return 922.50;
        if (salary <= 21250) return 945.00;
        if (salary <= 21750) return 967.50;
        if (salary <= 22250) return 990.00;
        if (salary <= 22750) return 1012.50;
        if (salary <= 23250) return 1035.00;
        if (salary <= 23750) return 1057.50;
        if (salary <= 24250) return 1080.00;
        if (salary <= 24750) return 1102.50;
        return 1125.00;
    }

   @Override
    public double calculatePhilHealth() {
        double premium = getBasicSalary() * 0.03;
     if (premium > 1800) premium = 1800; 
         return premium / 2; // Employee share
    }

    @Override
    public double calculatePagIbig() {
        double salary = getBasicSalary();
        double rate = (salary <= 1500) ? 0.01 : 0.02; // 1% for low earners, 2% otherwise
        double contribution = salary * rate;
        return Math.min(contribution, 100.00); // Cannot exceed 100
    }
}
