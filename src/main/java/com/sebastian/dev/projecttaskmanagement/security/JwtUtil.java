package com.sebastian.dev.projecttaskmanagement.security;

import com.sebastian.dev.projecttaskmanagement.repository.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningkey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Collection<Role> roles) {
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> rolesList = roles.stream().map(Role::toString).toList();//convert roles to String.
        extraClaims.put("roles", rolesList);

        return buildToken(username, extraClaims);
    }

    private String buildToken(String username, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .expiration(new Date(now + expiration))
                .issuedAt(new Date(now))
                .signWith(getSigningkey(), Jwts.SIG.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimFunction){
        final Claims claims = extractAllClaims(token);
        return claimFunction.apply(claims);
    }
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        Date expDate = extractClaim(token, Claims::getExpiration);
        return (expDate.before(new Date()));
    }
}
