package com.hab.emmaus.infrastructure.security;


import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    public static final String TOKEN_TYPE = "token_type";
    public static final String ROLES = "roles";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtService(
            @Value("${app.jwt-keys.private-key-path}") String privateKeyPath,
            @Value("${app.jwt-keys.public-key-path}") String publicKeyPath
    ) throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey(privateKeyPath);
        this.publicKey = KeyUtils.loadPublicKey(publicKeyPath);
    }

    public String generateAccessToken(final UUID userPublicId, final Set<String> roles) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "ACCESS_TOKEN",
                "roles", roles
        );
        return buildToken(userPublicId, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final UUID userPublicId) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(userPublicId, claims, this.refreshTokenExpiration);
    }

    public String buildToken(final UUID userPublicId, final Map<String, Object> claims, final long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userPublicId.toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(final String token, final UUID expectedUserId) {
        final UUID userId = extractUserPublicId(token);
        return userId.equals(expectedUserId) && !isTokenExpired(token);
    }

    public UUID extractUserPublicId(final String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public Set<String> extractRoles(String token) {
        List<?> raw = extractClaims(token).get(ROLES, List.class);

        if (raw == null) return Set.of();

        return raw.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    public boolean isAccessToken(String token) {
        return "ACCESS_TOKEN".equals(
                extractClaims(token).get(TOKEN_TYPE, String.class)
        );
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH_TOKEN".equals(
                extractClaims(token).get(TOKEN_TYPE, String.class)
        );
    }


    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }


}
