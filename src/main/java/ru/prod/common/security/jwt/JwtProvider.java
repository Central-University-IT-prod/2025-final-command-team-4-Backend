package ru.prod.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtProvider(@Value("${jwt.secret.access}") String accessSecret, @Value("${jwt.secret.refresh}") String refreshSecret) {
        this.accessSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
    }

    public String generateToken(UUID accountId, boolean isRefresh) {
        String jti = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Instant expirationInstant = isRefresh ?
                now.plusDays(31).atZone(ZoneId.systemDefault()).toInstant() :
                now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .id(jti)
                .subject(accountId.toString())
                .issuedAt(new Date())
                .expiration(Date.from(expirationInstant))
                .signWith(isRefresh ? refreshSecret : accessSecret)
                .compact();
    }

    public Claims extractAccessClaims(String jwtToken) {
        return extractAllClaims(jwtToken, accessSecret);
    }

    public Claims extractRefreshClaims(String jwtToken) {
        return extractAllClaims(jwtToken, refreshSecret);
    }

    private Claims extractAllClaims(String jwtToken, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean isValidAccessToken(String jwtToken) {
        return isValidToken(jwtToken, accessSecret);
    }

    public boolean isValidRefreshToken(String jwtToken) {
        return isValidToken(jwtToken, refreshSecret);
    }

    public boolean isValidToken(String jwtToken, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(jwtToken);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
