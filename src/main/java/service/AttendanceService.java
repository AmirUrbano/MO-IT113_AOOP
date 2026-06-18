/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.AttendanceDAO;
import model.AttendanceRecord;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;


public class AttendanceService {
    private final List<AttendanceRecord> attendanceRecords;
    private final AttendanceDAO attendanceDAO;
    private static AttendanceService instance;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    
    private AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.attendanceRecords = this.attendanceDAO.load();
    }
    
    public static AttendanceService getInstance() {
        if (instance == null) instance = new AttendanceService();
        return instance;
    }
    
    public List<AttendanceRecord> getRecordsInRange(String employeeId, LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return attendanceRecords.stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .filter(r -> {
                    LocalDate d = LocalDate.parse(r.getDate());
                    return !d.isBefore(start) && !d.isAfter(end);
                })
                .collect(Collectors.toList());
    }
    
 
    public double getHoursWorked(String employeeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        return attendanceRecords.stream()
                .filter(record -> record.getEmployeeId().equals(employeeId))
                .filter(record -> {
                    LocalDate date = LocalDate.parse(record.getDate(), DATE_FORMATTER);
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .mapToDouble(AttendanceRecord::getHoursWorked)
                .sum();
    }

    public int getMinutesLate(String employeeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        return attendanceRecords.stream()
                .filter(record -> record.getEmployeeId().equals(employeeId))
                .filter(record -> {
                    LocalDate date = LocalDate.parse(record.getDate(), DATE_FORMATTER);
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .mapToInt(AttendanceRecord::getLateMinutes)
                .sum();
    }

    public int getDaysLate(String employeeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        return (int) attendanceRecords.stream()
                .filter(record -> record.getEmployeeId().equals(employeeId))
                .filter(record -> {
                    LocalDate date = LocalDate.parse(record.getDate(), DATE_FORMATTER);
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .filter(AttendanceRecord::isLate)
                .count();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    public int getWeekNumber(LocalDate date) {
        return date.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
    }

    public LocalDate getStartOfWeek(LocalDate date) {
        return date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
    }

    public LocalDate getEndOfWeek(LocalDate date) {
        return date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);
    }
     // Get total number of records for debugging
    public int getTotalRecords() {
        return attendanceRecords.size();
    } 
  
    public String recordAttendance(String empId, boolean isTimeIn) {
        LocalDate today = LocalDate.now();
        String dateStr = today.toString(); // yyyy-MM-dd
        
        
        String nowTimeStr = java.time.LocalTime.now().format(TIME_FORMATTER);
       
        List<AttendanceRecord> todayRecords = getRecordsInRange(empId, today, today);
        
        if (isTimeIn) {
            if (!todayRecords.isEmpty()) {
                return "Already Timed In for today!";
            }
            
            
            AttendanceRecord newRec = new AttendanceRecord(empId, dateStr, nowTimeStr, "00:00");
            
            attendanceRecords.add(newRec);
            attendanceDAO.save(this.attendanceRecords); 
            
            return "Time In Recorded: " + nowTimeStr;
        } else {
            if (todayRecords.isEmpty()) {
                return "No Time In record found!";
            }
            
            AttendanceRecord record = todayRecords.get(0);
            if (!record.getLogOutTime().toString().equals("00:00")) {
                return "Already Timed Out!";
            }
            
          
            attendanceRecords.remove(record);
            
            
            AttendanceRecord updated = new AttendanceRecord(
                    empId, 
                    dateStr, 
                    record.getLogInTime().format(TIME_FORMATTER), 
                    nowTimeStr
            );
            
            attendanceRecords.add(updated);
            attendanceDAO.save(this.attendanceRecords); 
            
            return "Time Out Recorded: " + nowTimeStr;
        }
    }
} 