package com.agrireach.employee.dto;

import java.time.OffsetDateTime;

import java.util.UUID;

public class EmployeeListResponse {

    private UUID employeeId;

    private String employeeCode;

    private String username;

    private String fullName;

    private String email;

    private String phoneNumber;

    private boolean active;

    private OffsetDateTime createdAt;

    public EmployeeListResponse(

        UUID employeeId,

        String employeeCode,

        String username,

        String fullName,

        String email,

        String phoneNumber,

        boolean active,

        OffsetDateTime createdAt) {

        this.employeeId = employeeId;

        this.employeeCode = employeeCode;

        this.username = username;

        this.fullName = fullName;

        this.email = email;

        this.phoneNumber = phoneNumber;

        this.active = active;

        this.createdAt = createdAt;

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

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
