package com.demo.finance_tracker_backend.util;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration; // in milliseconds

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate JWT token with claims
    public String generateToken(UserEntity userEntity) {
        Claims claims = Jwts.claims();
        claims.put("userId", userEntity.getUserId());
        claims.put("username", userEntity.getUsername());
        claims.put("roles", List.of(userEntity.getRole().name()));
        claims.put("tokenVersion", String.valueOf(userEntity.getTokenVersion())); // store as string
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract roles
    @SuppressWarnings("unchecked")
	public Set<String> extractRoles(String token) {
        List<String> roles = extractClaims(token).get("roles", List.class);
        return new HashSet<>(roles);
    }

    // Extract userId
    public String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }
    
    // Extract tokenVersion
    public String extractTokenVersion(String token) {
        return extractClaims(token).get("tokenVersion", String.class);
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        Date expirationDate = extractClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    // Extract all claims with proper exception handling
    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("JWT token has expired, please login again");
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid JWT token");
        }
    }
}
