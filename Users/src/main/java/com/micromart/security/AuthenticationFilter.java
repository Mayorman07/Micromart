package com.micromart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micromart.models.data.CustomUserDetails;
import com.micromart.models.data.UserDto;
import com.micromart.models.requests.LoginRequest;
import com.micromart.models.responses.LoginResponse;
import com.micromart.services.UserService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final Environment environment;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(UserService userService, Environment environment,
                                AuthenticationManager authenticationManager, ObjectMapper objectMapper) {

        super(authenticationManager);
        this.environment = environment;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            LoginRequest loginCredentials = new ObjectMapper().readValue(req.getInputStream(), LoginRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String userId = userDetails.getUserId();
        userService.updateLastLoggedIn(userId);
        String tokenSecret = environment.getProperty("token.secret.key");
        if (tokenSecret == null) {
            throw new RuntimeException("Token secret key is missing in the configuration!");
        }
        byte[] secretKeyBytes = tokenSecret.getBytes(StandardCharsets.UTF_8);
        // Create a SecretKey using Keys.hMacShaKeyFor -> length of string determines algorithm to be used
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        long expirationTime = Long.parseLong(environment.getProperty("token.expiration.time"));

        Date expirationDate = Date.from(now.plusMillis(expirationTime));
        // Use the SecretKey to sign a JWT
        String token = Jwts.builder()
                .claim("scope", auth.getAuthorities())
                .subject(userDetails.getUserId())
                .expiration(expirationDate)
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        LoginResponse loginResponse = new LoginResponse(token, userDetails.getUserId(), expirationTime);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setStatus(HttpStatus.OK.value());
        res.getWriter().write(objectMapper.writeValueAsString(loginResponse));
        res.getWriter().flush();
    }
}