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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtClaimsParser {

    Jwt<?,?> jwtObject;

    public JwtClaimsParser(String jwt, String tokenSecret){

       this.jwtObject = parseJwt(jwt,tokenSecret);
    }

    Jwt<?,?> parseJwt(String jwtString , String tokenSecret){

        byte[] secretKeyBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey signInKey = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser jwtParser = Jwts.parser()
                .verifyWith(signInKey)
                .build();

        return jwtParser.parse(jwtString);

    }

    public Collection<? extends GrantedAuthority> getUserAuthorities(){
        Collection<Map<String, String>> scopes = ((Claims)jwtObject.getBody()).get("scope", List.class);
        return scopes.stream().map((scopeMap -> new SimpleGrantedAuthority(scopeMap.get("authority"))))
                .collect(Collectors.toList());

    }

    public String getJwtSubject(){
       return  ((Claims)jwtObject.getBody()).getSubject();
    }
}
