package com.unipapers.backend.Configurations.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Service class responsible for handling JWT (JSON Web Token) operations.
 * This includes generating access tokens, generating refresh tokens,
 * extracting claims, and validating tokens.
 */

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        // Decode the base64 secret and create the key
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT (JSON Web Token) access token based on the provided username and claims.
     *
     * @param studentNumber the student number for which the access token is to be generated
     * @param claims a map containing additional claims to include in the token
     * @return a signed JWT string representing the generated access token
     */
    public String generateAccessToken(String studentNumber, Map<String, Object> claims){

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(studentNumber)
                .claims(claims)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key) //set the signing key
                .compact();
    }

    /**
     * Overloaded method to generate a refresh token without the rememberMe parameter. This method is essential when rotating refresh tokens.
     * */
    public String generateRefreshToken(String studentNumber){

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(studentNumber)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts claims from the provided JWT token.
     * @param token - JWT token
     * @return Claims
     */
    public Claims extractClaims(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return the student Number extracted from the token
     */
    public String extractStudentNumber(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token the JWT token from which to extract the expiration date
     * @return the expiration date extracted from the token
     */
    public Instant extractExpirationDate(String token){
        return extractClaims(token).getExpiration().toInstant();
    }

    /**
     * Validates the provided JWT token by checking if the username extracted from the token matches
     * the username of the provided user and ensures that the token is not expired.
     *
     * @param userDetails the user details used to validate the token
     * @param token the JWT token to be validated
     * @return {@code true} if the token is valid (usernames match and the token is not expired),
     *         {@code false} otherwise
     */
    public boolean isTokenValid(UserDetails userDetails, String token){
        String studentNumber = extractStudentNumber(token);
        Instant exp = extractExpirationDate(token);

        return studentNumber.equals(userDetails.getUsername()) && exp.isAfter(Instant.now());
    }

}