package com.agrireach.employee.service;

import com.agrireach.common.ResourceConflictException;
import com.agrireach.common.ResourceNotFoundException;
import com.agrireach.employee.dto.EmployeeCreateRequest;
import com.agrireach.employee.dto.EmployeeListResponse;
import com.agrireach.employee.dto.EmployeeUpdateRequest;
import com.agrireach.employee.entity.Employee;
import com.agrireach.user.Role;
import com.agrireach.user.RoleRepository;
import com.agrireach.user.User;
import com.agrireach.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private static final Logger logger =
        LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
        EmployeeRepository employeeRepository,
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder) {

        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UUID createEmployee(EmployeeCreateRequest request) {
        logger.info(
            "Creating employee account with username: {} and employee code: {}",
            request.getUsername(),
            request.getEmployeeCode()
        );

        // 1. Check whether username already exists
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new ResourceConflictException(
                "Username already exists"
            );
        }
        // 2. Check whether employee code already exists
        String employeeCode = request.getEmployeeCode().trim();
        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new ResourceConflictException(
                "Employee code already exists"
            );
        }
        // 3. Check email uniqueness when provided
        String email = request.getEmail();
        if (email != null && !email.isBlank()) {
            email = email.trim();
            if (userRepository.existsByEmail(email)) {
                throw new ResourceConflictException(
                    "Email already exists"
                );
            }
        } else {
            email = null;
        }
        // 4. Retrieve the EMPLOYEE role
        Role employeeRole = roleRepository.findByName("EMPLOYEE")
            .orElseThrow(() -> new IllegalStateException(
                "EMPLOYEE role not found in database"
            ));
        // 5. Create the user account
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );
        user.setEmail(email);
        user.setActive(true);
        user.setMustChangePassword(true);
        user.getRoles().add(employeeRole);

        logger.info(
            "Creating user account for employee. username={}, email={}",
            username,
            email
        );
        User savedUser = userRepository.save(user);
        // 6. Create the employee profile
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setEmployeeCode(employeeCode);
        employee.setFullName(request.getFullName().trim());
        employee.setPhoneNumber(request.getPhoneNumber());

        logger.info(
            "Creating employee profile. employeeCode={}, fullName={}",
            employeeCode,
            request.getFullName().trim()
        );
        Employee savedEmployee = employeeRepository.save(employee);
        // 7. Return the new employee ID
        return savedEmployee.getId();
    }

    @Transactional(readOnly = true)
    public List<EmployeeListResponse> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository
            .findAllByOrderByCreatedAtDesc()
            .stream()
            .map(employee -> new EmployeeListResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getUser().getUsername(),
                employee.getFullName(),
                employee.getUser().getEmail(),
                employee.getPhoneNumber(),
                employee.isActive(),
                employee.getCreatedAt()
            ))
            .toList();
    }

    @Transactional
    public EmployeeListResponse updateEmployee(
        UUID employeeId,
        EmployeeUpdateRequest request) {
        logger.info(
            "Updating employee details. employeeId={}, fullName={}, email={}, phoneNumber={}",
            employeeId,
            request.getFullName(),
            request.getEmail(),
            request.getPhoneNumber()
        );
        // Find employee
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Employee not found"));
        User user = employee.getUser();
        // Normalize email
        String email = request.getEmail();
        if (email != null) {
            email = email.trim();
            if (email.isBlank()) {
                email = null;
            }
        }
        // Check whether email belongs to another user
        if (email != null &&
            !email.equalsIgnoreCase(user.getEmail()) &&
            userRepository.existsByEmail(email)) {
            throw new ResourceConflictException(
                "Email address already exists");
        }
        // Update employee details
        employee.setFullName(request.getFullName().trim());
        String phone = request.getPhoneNumber();
        employee.setPhoneNumber(
            phone == null || phone.isBlank()
                ? null
                : phone.trim()
        );
        user.setEmail(email);

        logger.info(
            "Saving updated employee details. employeeId={}, fullName={}, email={}, phoneNumber={}",
            employeeId,
            request.getFullName(),
            email,
            request.getPhoneNumber()
        );
        // Return updated employee details
        return new EmployeeListResponse(
            employee.getId(),
            employee.getEmployeeCode(),
            user.getUsername(),
            employee.getFullName(),
            user.getEmail(),
            employee.getPhoneNumber(),
            employee.isActive(),
            employee.getCreatedAt()
        );
    }

    @Transactional
    public EmployeeListResponse updateEmployeeStatus(UUID employeeId, boolean active) {

        logger.info(
            "Updating employee status. employeeId={}, active={}",
            employeeId,
            active
        );
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setActive(active);

        User user = employee.getUser();
        user.setActive(active);


        logger.info(
            "Saving updated employee status. employeeId={}, active={}",
            employeeId,
            active
        );
        return new EmployeeListResponse(
            employee.getId(),
            employee.getEmployeeCode(),
            user.getUsername(),
            employee.getFullName(),
            user.getEmail(),
            employee.getPhoneNumber(),
            employee.isActive(),
            employee.getCreatedAt()
        );

    }

    @Transactional(readOnly = true)
    public EmployeeListResponse getEmployeeById(UUID employeeId) {
        logger.info("Fetching employee details. employeeId={}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Employee not found"));
        User user = employee.getUser();

        logger.info(
            "Fetched employee details. employeeId={}, username={}, fullName={}",
            employeeId,
            user.getUsername(),
            employee.getFullName()
        );
        return new EmployeeListResponse(
            employee.getId(),
            employee.getEmployeeCode(),
            user.getUsername(),
            employee.getFullName(),
            user.getEmail(),
            employee.getPhoneNumber(),
            employee.isActive(),
            employee.getCreatedAt()
        );
    }
}