package com.agrireach.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expiration;

    public JwtService(
        JwtEncoder jwtEncoder,
        @Value("${app.security.jwt.expiration}") long expiration) {
        this.jwtEncoder = jwtEncoder;
        this.expiration = expiration;
    }

    public String generateToken(
        String username,
        UUID userId,
        Collection<String> roles,
        boolean mustChangePassword) {
        Instant now = Instant.now();
        // 1. Define JWT claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("agrireach")
            .issuedAt(now)
            .expiresAt(now.plusMillis(expiration))
            .subject(username)
            .claim("userId", userId.toString())
            .claim("roles", roles)
            .claim("mustChangePassword", mustChangePassword)
            .build();
        // 2. Define signing algorithm
        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();
        // 3. Generate signed JWT
        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}