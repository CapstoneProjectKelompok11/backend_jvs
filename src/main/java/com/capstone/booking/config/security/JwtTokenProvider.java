package com.capstone.booking.config.security;

import com.capstone.booking.domain.dao.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // batas waktu ekspirasi jwt dalam millisecond
    @Value("${token.expiration.hour:1}")
    private Long expiration;

    public String generateToken(Authentication authentication){
        final User user = (User) authentication.getPrincipal();

        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + expiration * 3600000L);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getUsername());
        claims.put("roles", user.getRoles());

        return Jwts.builder()
                .setId(user.getId().toString())
                .setSubject(user.getUsername())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid Jwt Signature: {} ", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid Jwt Token: {} ", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired Jwt Token: {} ", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported Jwt Token: {} ", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Jwt claim string is empty: {} ", ex.getMessage());
        }
        return false;
    }
    public String getEmail(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("email").toString();
    }
}
