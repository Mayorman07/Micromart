package com.micromart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micromart.models.data.CustomUserDetails;
import com.micromart.models.requests.LoginRequest;
import com.micromart.models.responses.LoginResponse;
import com.micromart.services.UserService;
import com.micromart.utils.JwtUtils;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final Environment environment;
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(UserService userService, Environment environment,
                                AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtUtils jwtUtils) {

        super(authenticationManager);
        this.environment = environment;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.jwtUtils = jwtUtils;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            LoginRequest loginCredentials = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
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
        String accessToken = jwtUtils.generateAccessToken(userDetails.getEmail(), auth.getAuthorities());
        String refreshToken = userService.createRefreshToken(userId);
        long expirationTime = Long.parseLong(environment.getProperty("token.expiration.time"));
        LoginResponse loginResponse = new LoginResponse(accessToken, userDetails.getUserId(), refreshToken, expirationTime);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setStatus(HttpStatus.OK.value());
        res.getWriter().write(objectMapper.writeValueAsString(loginResponse));
        res.getWriter().flush();
    }
}