package com.parser.JwtAuthorities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtClaimsParser {

    private final Jwt<?, ?> jwtObject;

    public JwtClaimsParser(String jwt, String tokenSecret) {
        this.jwtObject = parseJwt(jwt, tokenSecret);
    }

    Jwt<?, ?> parseJwt(String jwtString, String tokenSecret) {
        byte[] secretKeyBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey signInKey = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser jwtParser = Jwts.parser()
                .verifyWith(signInKey)
                .build();

        return jwtParser.parse(jwtString);
    }

    public Collection<? extends GrantedAuthority> getUserAuthorities() {
        // 🛠️ USE getPayload() instead of getBody() for newer JJWT versions
        Claims claims = (Claims) jwtObject.getPayload();

        // 🔍 Retrieve the "scope" claim
        List<Map<String, String>> scopes = (List<Map<String, String>>) claims.get("scope");

        if (scopes == null || scopes.isEmpty()) {
            return new ArrayList<>();
        }

        return scopes.stream()
                // 🎯 Maps the internal "authority" key from the JWT JSON to SimpleGrantedAuthority
                .map(scopeMap -> new SimpleGrantedAuthority(scopeMap.get("authority")))
                .collect(Collectors.toList());
    }

    public String getJwtSubject() {
        // 🛠️ USE getPayload() here as well
        return ((Claims) jwtObject.getPayload()).getSubject();
    }
}