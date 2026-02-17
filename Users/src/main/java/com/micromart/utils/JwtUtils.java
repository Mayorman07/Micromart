package com.micromart.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final Environment environment;

    public String generateAccessToken(String userEmail, Collection<? extends GrantedAuthority> authorities) {
        String tokenSecret = environment.getProperty("token.secret.key");
        byte[] secretKeyBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();
        long expirationTime = Long.parseLong(environment.getProperty("token.expiration.time"));
        Date expirationDate = Date.from(now.plusMillis(expirationTime));

        return Jwts.builder()
                .claim("scope", authorities)
                .subject(userEmail)
                .expiration(expirationDate)
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();
    }
}
