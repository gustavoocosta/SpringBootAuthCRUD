
package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class JwtUtil {
    private static Key key() {
        try {
            String secret = System.getenv().getOrDefault("JWT_SECRET", System.getProperty("jwt.secret", "default-secret-key-please-change"));
            byte[] derived = secret.getBytes(StandardCharsets.UTF_8);
            if (derived.length < 32) { // derive key for short secrets
                PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), "salt1234".getBytes(StandardCharsets.UTF_8), 1000, 256);
                derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            }
            return Keys.hmacShaKeyFor(derived);
        } catch(Exception e) {
            throw new RuntimeException("Failed to create JWT key", e);
        }
    }
    private static final long EXP_MS = 1000L * 60 * 60 * 4;

    public static String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(key())
                .compact();
    }

    public static Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }
}
