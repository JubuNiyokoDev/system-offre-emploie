package com.jobportal.job_portal.security;

import com.jobportal.job_portal.common.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final long accessTokenExpiration = 5 * 60 * 1000;
    private final long refreshTokenExpiration = 24 * 60 * 60 * 1000;

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpiration);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration);
    }

    private String generateToken(String subject, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            String username = extractUsername(refreshToken);
            if (validateToken(refreshToken, username)) {
                return generateAccessToken(username);
            }
            throw new ApiException("Token de rafraîchissement invalide ou expiré", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ApiException("Token de rafraîchissement mal formé", HttpStatus.BAD_REQUEST);
        }
    }

    public String rotateRefreshToken(String refreshToken) {
        try {
            String username = extractUsername(refreshToken);
            if (validateToken(refreshToken, username)) {
                return generateRefreshToken(username);
            }
            throw new ApiException("Token de rafraîchissement invalide ou expiré", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ApiException("Token de rafraîchissement mal formé", HttpStatus.BAD_REQUEST);
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            throw new ApiException("Token JWT invalide", HttpStatus.BAD_REQUEST);
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new ApiException("Token JWT invalide", HttpStatus.BAD_REQUEST);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ApiException("Erreur lors du parsing du token JWT", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            throw new ApiException("Token invalide ou mal formé", HttpStatus.BAD_REQUEST);
        }
    }
}