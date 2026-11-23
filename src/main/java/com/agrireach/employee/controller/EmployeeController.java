package com.agrireach.employee.controller;

import com.agrireach.auth.service.AuthService;
import com.agrireach.employee.service.EmployeeService;
import com.agrireach.employee.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private static final Logger logger =
        LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeCreateResponse> createEmployee(
        @Valid @RequestBody EmployeeCreateRequest request) {
        logger.info("Creating employee account with username: {}", request.getUsername());
        UUID employeeId = employeeService.createEmployee(request);
        EmployeeCreateResponse response =
            new EmployeeCreateResponse(
                employeeId,
                request.getEmployeeCode(),
                request.getUsername(),
                request.getFullName(),
                "Employee account created successfully"
            );

        logger.info("Employee account created successfully with ID: {}", employeeId);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<EmployeeListResponse>> getAllEmployees() {
        logger.info("Fetching all employees");
        List<EmployeeListResponse> employees = employeeService.getAllEmployees();

        logger.info("Fetched {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeListResponse> updateEmployee(@PathVariable UUID employeeId,
        @Valid @RequestBody EmployeeUpdateRequest request) {

        logger.info("Updating employee with ID: {}", employeeId);
        EmployeeListResponse employeeListResponse = employeeService.updateEmployee(employeeId, request);


        logger.info("Employee with ID: {} updated successfully", employeeId);
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

        logger.info("Fetching employee with ID: {}", employeeId);
        EmployeeListResponse response =
            employeeService.getEmployeeById(employeeId);

        logger.info("Fetched employee with ID: {}", employeeId);
        return ResponseEntity.ok(response);
    }
}