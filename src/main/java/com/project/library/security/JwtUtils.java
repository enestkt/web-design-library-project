package com.project.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // application.properties içindeki 'library.app.jwtSecret' değerini okur
    @Value("${library.app.jwtSecret}")
    private String jwtSecret;

    // application.properties içindeki 'library.app.jwtExpirationMs' değerini okur (86400000 = 24 saat)
    @Value("${library.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    // Ortak imzalama anahtarını oluşturur
    private Key getSigningKey() {
        // Properties'den gelen stringi byte dizisine çevirip anahtara dönüştürür
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ===========================================================
    // 1) TOKEN GENERATE (UserDetails ile) -> React / Frontend için
    // ===========================================================
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // email bilgisi
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===========================================================
    // 2) TOKEN GENERATE (Email ile) -> AuthServiceImpl kullanımı için
    // ===========================================================
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
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
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}