package com.project.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // TEK BİR SECRET KEY KULLANIYORUZ
    private static final String SECRET_KEY =
            "library_project_secret_key_library_project_secret_key"; // 64+ chars

    // 24 saat
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;


    // COMMON SIGN KEY
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    // ===========================================================
    // 1) TOKEN GENERATE (UserDetails ile)  -> React / Mobile için
    // ===========================================================
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())   // email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // ===========================================================
    // 2) TOKEN GENERATE (Email ile) -> Thymeleaf login/register için
    // ===========================================================
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // ===========================================================
    // EXTRACT EMAIL FROM TOKEN
    // ===========================================================
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // ===========================================================
    // VALIDATE TOKEN
    // ===========================================================
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername());
    }
}
