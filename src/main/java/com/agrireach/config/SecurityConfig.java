package com.agrireach.config;

import com.agrireach.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
        CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter =
            new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
            // Login and health check are public
            .requestMatchers(
                "/api/v1/auth/login",
                "/actuator/health"
            ).permitAll()
            // Users must be authenticated to change passwords
            .requestMatchers("/api/v1/auth/change-password")
            .authenticated()
            // Only admins who have changed their initial password
            // can manage employees
            .requestMatchers("/api/v1/employees/**")
            .access((authentication, context) -> {
                var currentAuth = authentication.get();
                if (!(currentAuth instanceof JwtAuthenticationToken jwtAuth)) {
                    return new AuthorizationDecision(false);
                }
                Boolean mustChange = jwtAuth.getToken()
                    .getClaim("mustChangePassword");
                boolean isAdmin = currentAuth.getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return new AuthorizationDecision(
                    Boolean.FALSE.equals(mustChange) && isAdmin
                );
            })
            // All other APIs require authentication and a completed
            // initial password change
            .anyRequest()
            .access((authentication, context) -> {
                var currentAuth = authentication.get();
                if (!(currentAuth instanceof JwtAuthenticationToken jwtAuth)) {
                    return new AuthorizationDecision(false);
                }
                Boolean mustChange = jwtAuth.getToken()
                    .getClaim("mustChangePassword");
                return new AuthorizationDecision(
                    Boolean.FALSE.equals(mustChange)
                );
            })
        )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter()
                    )
                )
            );
        return http.build();
    }
}