package com.agrireach.auth.controller;

import com.agrireach.auth.service.AuthService;
import com.agrireach.auth.dto.ChangePasswordRequest;
import com.agrireach.auth.dto.LoginRequest;
import com.agrireach.auth.dto.LoginResponse;
import com.agrireach.video.service.VideoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger =
        LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(
            jwt.getSubject(),
            request
        );
        return ResponseEntity.ok(
            "Password changed successfully. Please log in again."
        );
    }
}