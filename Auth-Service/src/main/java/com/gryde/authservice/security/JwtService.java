package com.gryde.authservice.security;

import com.gryde.authservice.dto.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String base64Secret;

    @Value("${spring.security.jwt.issuer}")
    private String issuer;

    @Value("${spring.security.jwt.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;

    public String generateAccessToken(UUID userId, UserRole role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("typ", "access")
                .claim("role", role)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRegistrationToken(UUID verificationId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(10, ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(issuer)
                .subject(verificationId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("typ", "registration")
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRegistrationToken(String token) {
        Claims claims = parseToken(token);
        requireTokenType(claims, "registration");
        return claims;
    }

    public Claims parseAccessToken(String token) {
        Claims claims = parseToken(token);
        requireTokenType(claims, "access");
        return claims;
    }

    private void requireTokenType(Claims claims, String expectedType) {
        String actualType = claims.get("typ", String.class);

        if (!expectedType.equals(actualType)) {
            throw new JwtException("Invalid token type");
        }
    }

    public UUID extractVerificationId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
