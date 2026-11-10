package com.gerenciadorPet.service;

import com.gerenciadorPet.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final long TOKEN_LIFETIME_MINUTES = 15;
    private final SecretKey signingKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        try {
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("app.jwt.secret deve ser uma chave Base64 com pelo menos 32 bytes", exception);
        }
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("tokenType", "access")
                .claim("userId", Long.toString(user.getId()))
                .claim("role", user.getAdmin().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_LIFETIME_MINUTES, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public String generatePasswordResetToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .id(UUID.randomUUID().toString())
                .claim("tokenType", "password_reset")
                .claim("userId", Long.toString(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_LIFETIME_MINUTES, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        Claims claims = parse(token);
        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new JwtException("Invalid access token");
        }
        return claims;
    }

    public Claims parsePasswordResetToken(String token) {
        try {
            Claims claims = parse(token);
            if (!"password_reset".equals(claims.get("tokenType", String.class)) ||
                    claims.getId() == null || claims.get("userId", String.class) == null) {
                throw new JwtException("Invalid password reset token");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado");
        }
    }

    public long getAccessTokenLifetimeSeconds() {
        return TOKEN_LIFETIME_MINUTES * 60;
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
