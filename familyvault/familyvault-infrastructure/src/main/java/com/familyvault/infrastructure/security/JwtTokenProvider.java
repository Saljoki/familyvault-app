package com.familyvault.infrastructure.security;

import com.familyvault.core.application.port.out.persistence.RefreshTokenRepository;
import com.familyvault.core.application.port.out.security.TokenPort;
import com.familyvault.core.domain.model.user.UserId;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider implements TokenPort {

    private final SecretKey secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:15m}") Duration accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:7d}") Duration refreshTokenExpiration,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String generateAccessToken(UserId userId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserId userId) {
        UUID tokenId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiration);

        String token = Jwts.builder()
                .id(tokenId.toString())
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();

        // Store token hash in database
        String tokenHash = hashToken(token);
        refreshTokenRepository.save(tokenId, userId, tokenHash, null, null, expiry);

        return token;
    }

    @Override
    public Optional<UserId> validateAccessToken(String token) {
        try {
            Claims claims = parseToken(token);

            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                log.debug("Invalid token type: {}", type);
                return Optional.empty();
            }

            return Optional.of(UserId.of(claims.getSubject()));
        } catch (JwtException e) {
            log.debug("Invalid access token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserId> validateRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);

            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                log.debug("Invalid token type: {}", type);
                return Optional.empty();
            }

            // Check if token is in database and not revoked
            String tokenHash = hashToken(token);
            Optional<RefreshTokenRepository.RefreshTokenData> tokenData =
                    refreshTokenRepository.findByTokenHash(tokenHash);

            if (tokenData.isEmpty() || !tokenData.get().isValid()) {
                log.debug("Refresh token not found or revoked");
                return Optional.empty();
            }

            return Optional.of(UserId.of(claims.getSubject()));
        } catch (JwtException e) {
            log.debug("Invalid refresh token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration.toSeconds();
    }

    @Override
    public void revokeRefreshToken(String token) {
        String tokenHash = hashToken(token);
        refreshTokenRepository.revoke(tokenHash);
    }

    @Override
    public void revokeAllUserTokens(UserId userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
