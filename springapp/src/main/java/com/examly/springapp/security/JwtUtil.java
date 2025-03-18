package com.examly.springapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.examly.springapp.services.TokenBlacklistService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating, validating, and extracting information from JSON Web Tokens (JWT).
 * This class uses a secret key and expiration time configured via application properties.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // Expected in seconds, converted to milliseconds internally

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token The JWT token to parse.
     * @return The username contained in the token.
     * @throws IllegalArgumentException if the token is null or empty.
     */
    public String getUsernameFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token The JWT token to parse.
     * @return The expiration date of the token.
     * @throws IllegalArgumentException if the token is null or empty.
     */
    public Date getExpirationDateFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided resolver function.
     *
     * @param token The JWT token to parse.
     * @param claimsResolver Function to resolve the desired claim.
     * @param <T> The type of the claim.
     * @return The resolved claim value.
     * @throws IllegalArgumentException if the token is invalid or cannot be parsed.
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token and returns all claims.
     *
     * @param token The JWT token to parse.
     * @return The claims contained in the token.
     * @throws IllegalArgumentException if the token is invalid, malformed, or the signature is invalid.
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT signature: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * Checks if the token has expired.
     *
     * @param token The JWT token to check.
     * @return true if the token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    /**
     * Generates a new JWT token for the given user details.
     *
     * @param userDetails The user details to encode in the token.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Generates a JWT token with the specified claims and subject.
     *
     * @param claims The claims to include in the token.
     * @param subject The subject (username) of the token.
     * @return The generated JWT token.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)) // Convert seconds to milliseconds
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Validates the JWT token against the user details.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to compare against.
     * @return true if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (tokenBlacklistService == null || tokenBlacklistService.isTokenBlacklisted(token)) {
            return false;
        }
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}