package com.agrireach.security;

import com.agrireach.user.User;
import com.agrireach.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {

        // 1. Find user in PostgreSQL
        User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "User not found: " + username
                )
            );

        // 2. Convert application roles into Spring Security authorities
        var authorities = user.getRoles()
            .stream()
            .map(role -> new SimpleGrantedAuthority(
                "ROLE_" + role.getName()
            ))
            .collect(Collectors.toSet());

        // 3. Return Spring Security user details
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(authorities)
            .disabled(!user.isActive())
            .build();
    }
}