package com.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // SignatureAlgorithm.ES256

    public String generateToken(String username){
        //1시간
        long expiration = 1000L * 60 * 60;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date( ) )
                .setExpiration( new Date(System.currentTimeMillis() + expiration) )
                .signWith(key)
                .compact();
    }

    // util/JwtTokenProvider.java
    public String generateAccessToken(String username) {
        long expiration_30m = 1000L * 60 * 60;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration_30m)) // 30분
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username) {
        long expiration_7d = 1000L * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration_7d)) // 7일
                .signWith(key)
                .compact();
    }


    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
