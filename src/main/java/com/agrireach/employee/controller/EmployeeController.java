package com.agrireach.employee.controller;

import com.agrireach.employee.service.EmployeeService;
import com.agrireach.employee.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeCreateResponse> createEmployee(
        @Valid @RequestBody EmployeeCreateRequest request) {
        UUID employeeId = employeeService.createEmployee(request);
        EmployeeCreateResponse response =
            new EmployeeCreateResponse(
                employeeId,
                request.getEmployeeCode(),
                request.getUsername(),
                request.getFullName(),
                "Employee account created successfully"
            );
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<EmployeeListResponse>> getAllEmployees() {

        List<EmployeeListResponse> employees = employeeService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeListResponse> updateEmployee(@PathVariable UUID employeeId,
        @Valid @RequestBody EmployeeUpdateRequest request) {

        EmployeeListResponse employeeListResponse = employeeService.updateEmployee(employeeId, request);

        return ResponseEntity.ok(employeeListResponse);
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<EmployeeListResponse> updateEmployeeStatus(@PathVariable UUID employeeId,
        @Valid @RequestBody EmployeeStatusRequest request) {

        EmployeeListResponse employeeListResponse =
            employeeService.updateEmployeeStatus(employeeId, request.getActive());

        return ResponseEntity.ok(employeeListResponse);
    }

    @GetMapping("/get/{employeeId}")
    public ResponseEntity<EmployeeListResponse> getEmployeeById(
        @PathVariable UUID employeeId) {
        EmployeeListResponse response =
            employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(response);
    }
}