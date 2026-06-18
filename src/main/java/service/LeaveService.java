/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.LeaveDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.LeaveRequest;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Amir
 */
public class LeaveService {
    private static final Logger logger = Logger.getLogger(LeaveService.class.getName());
    private static LeaveService instance;
    private final List<LeaveRequest> leaveRequests;
    private final LeaveDAO leaveDAO;

    private LeaveService() {
        this.leaveDAO = new LeaveDAO();
        this.leaveRequests = this.leaveDAO.load();
        logger.info("LeaveService initialized with " + leaveRequests.size() + " records.");
    }

    public static LeaveService getInstance() {
        if (instance == null) {
            instance = new LeaveService();
        }
        return instance;
    }

    public void fileLeave(LeaveRequest request) {
        leaveRequests.add(request);
        leaveDAO.save(leaveRequests); 
    }

    public void applyForLeave(String empId, String name, String start, String end, String type) {
        String reqId = "LR-" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        LeaveRequest newRequest = new LeaveRequest(reqId, empId, name, start, end, type, "Pending");
        fileLeave(newRequest);
        logger.info("Successfully filed leave " + reqId + " for " + name);
    }
    
    public List<LeaveRequest> getAllLeaveRequests() {
        return new ArrayList<>(leaveRequests);
    }
    
    public boolean updateLeaveStatus(String requestId, String newStatus) {
        for (LeaveRequest lr : leaveRequests) {
            if (lr.getRequestId().equals(requestId)) {
                lr.setStatus(newStatus);
                leaveDAO.save(leaveRequests); 
                logger.info("Leave Request " + requestId + " updated to: " + newStatus);
                return true;
            }
        }
        return false;
    }

    public long calculateLeaveDays(String start, String end) {
        LocalDate d1 = LocalDate.parse(start);
        LocalDate d2 = LocalDate.parse(end);
        return ChronoUnit.DAYS.between(d1, d2) + 1; // Inclusive of start/end
    }

    public List<LeaveRequest> getLeaveHistoryForEmployee(String empId) {
        return leaveRequests.stream()
                .filter(lr -> lr.getEmployeeId().equals(empId))
                .collect(Collectors.toList());
    }
    
    public List<LeaveRequest> getLeavesByEmployeeId(String empId) {
        return getLeaveHistoryForEmployee(empId);
    }
}
