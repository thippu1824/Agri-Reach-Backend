package com.agrireach.config;

import com.agrireach.user.Role;
import com.agrireach.user.RoleRepository;
import com.agrireach.user.User;
import com.agrireach.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class AdminBootstrap {
    @Bean
    public ApplicationRunner createInitialAdmin(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.bootstrap-admin.username:}") String username,
        @Value("${app.bootstrap-admin.password:}") String password,
        @Value("${app.bootstrap-admin.email:}") String email) {
        return args -> {
            if (username.isBlank() || password.isBlank()) {
                System.out.println(
                    "Admin bootstrap skipped: credentials not configured."
                );
                return;
            }
            if (userRepository.existsByUsername(username.trim())) {
                System.out.println(
                    "Admin bootstrap skipped: configured username already exists."
                );
                return;
            }
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() ->
                    new IllegalStateException("ADMIN role not found")
                );
            User admin = new User();
            admin.setUsername(username.trim());
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setEmail(email.isBlank() ? null : email.trim());
            admin.setActive(true);
            admin.setMustChangePassword(true);
            admin.setRoles(new HashSet<>());
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            System.out.println(
                "Initial AgriReach admin account created successfully."
            );
        };
    }
}