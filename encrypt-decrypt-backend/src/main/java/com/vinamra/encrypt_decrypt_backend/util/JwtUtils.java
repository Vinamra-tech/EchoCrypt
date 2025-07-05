package com.vinamra.encrypt_decrypt_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret; // Base64-encoded secret key

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    
    public String getUserNameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.debug("Token for {} expired at {}", getUserNameFromToken(token), expiration);
        }
        return expired;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUserNameFromToken(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

            if (!isValid) {
                log.warn("Token validation failed for user {}. Token username: {}, IsExpired: {}",
                        userDetails.getUsername(), username, isTokenExpired(token));
            }

            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}
