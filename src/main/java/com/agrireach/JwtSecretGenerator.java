package com.agrireach;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        byte[] secret = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secret);
        String encodedSecret =
            Base64.getEncoder().encodeToString(secret);
        System.out.println("JWT Secret: " + encodedSecret);
    }
}