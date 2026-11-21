package com.agrireach.employee.dto;

import java.util.UUID;
public class EmployeeCreateResponse {

    private UUID employeeId;
    private String employeeCode;
    private String username;
    private String fullName;
    private String message;

    public EmployeeCreateResponse(
        UUID employeeId,
        String employeeCode,
        String username,
        String fullName,
        String message) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.username = username;
        this.fullName = fullName;
        this.message = message;
    }
    public UUID getEmployeeId() {
        return employeeId;
    }
    public String getEmployeeCode() {
        return employeeCode;
    }
    public String getUsername() {
        return username;
    }
    public String getFullName() {
        return fullName;
    }
    public String getMessage() {
        return message;
    }
}