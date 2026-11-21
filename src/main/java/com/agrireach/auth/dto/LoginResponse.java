package com.agrireach.auth.dto;

import java.util.List;

public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private long expiresIn;

    private String username;

    private String fullName;

    private String role;

    private boolean mustChangePassword;

    public LoginResponse(
        String accessToken,
        long expiresIn,
        String username,
        String fullName,
        String role,
        boolean mustChangePassword) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.mustChangePassword = mustChangePassword;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }
}