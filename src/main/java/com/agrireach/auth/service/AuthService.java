package com.agrireach.auth.service;

import com.agrireach.auth.dto.ChangePasswordRequest;
import com.agrireach.auth.dto.LoginRequest;
import com.agrireach.auth.dto.LoginResponse;
import com.agrireach.employee.entity.Employee;
import com.agrireach.employee.service.EmployeeRepository;
import com.agrireach.security.JwtService;
import com.agrireach.user.User;
import com.agrireach.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final long expiration;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        AuthenticationManager authenticationManager,
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        JwtService jwtService,
        @Value("${app.security.jwt.expiration}") long expiration, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.jwtService = jwtService;
        this.expiration = expiration;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 1. Verify username and password
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername().trim(),
                    request.getPassword()
                )
            );
        // 2. Retrieve authenticated user
        User user = userRepository.findByUsername(
            authentication.getName()
        ).orElseThrow(() ->
            new IllegalStateException("Authenticated user not found")
        );
        // 3. Get the user's role
        List<String> roles = user.getRoles()
            .stream()
            .map(role -> "ROLE_" + role.getName())
            .toList();
        // 4. Generate JWT
        String token = jwtService.generateToken(
            user.getUsername(),
            user.getId(),
            roles,
            user.isMustChangePassword()
        );

        // 5. Get employee name if an employee profile exists
        String fullName = employeeRepository
            .findByUser_Username(user.getUsername())
            .map(Employee::getFullName)
            .orElse(user.getUsername());

        // 6. Return login response
        return new LoginResponse(
            token,
            expiration,
            user.getUsername(),
            fullName,
            roles.get(0).replace("ROLE_", ""),
            user.isMustChangePassword()
        );
    }

    @Transactional
    public void changePassword(
        String username,
        ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(
            request.getCurrentPassword(),
            user.getPasswordHash())) {
            throw new IllegalArgumentException(
                "Current password is incorrect");
        }
        if (request.getCurrentPassword().equals(
            request.getNewPassword())) {
            throw new IllegalArgumentException(
                "New password must be different from current password");
        }
        user.setPasswordHash(
            passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}