package com.micromart.security;

import com.parser.JwtAuthorities.JwtClaimsParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * Intercepts HTTP requests to validate JWT authorization tokens.
 * Extracts the token from the request header, parses the claims, and populates
 * the Spring Security context with the user's authenticated identity and authorities.
 */
@Component
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Environment environment;

    /**
     * Constructs the AuthorizationFilter.
     *
     * @param authenticationManager The Spring Security authentication manager.
     * @param environment           The environment properties containing token configurations.
     */
    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String headerName = environment.getProperty("authorization.token.header.name");
        String headerPrefix = environment.getProperty("authorization.token.header.prefix");

        String authorizationHeader = req.getHeader(headerName);

        if (authorizationHeader == null || !authorizationHeader.startsWith(headerPrefix)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        // Sets the verified principal (email) and authorities into the Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    /**
     * Extracts and validates the JWT from the HTTP request header.
     *
     * @param req The incoming HTTP servlet request.
     * @return A UsernamePasswordAuthenticationToken if the token is valid, otherwise null.
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String headerName = environment.getProperty("authorization.token.header.name");
        String headerPrefix = environment.getProperty("authorization.token.header.prefix");

        String authorizationHeader = req.getHeader(headerName);

        if (authorizationHeader == null) {
            return null;
        }

        // Strip the configured prefix (e.g., "Bearer ") to isolate the raw JWT
        String token = authorizationHeader.replace(Objects.requireNonNull(headerPrefix), "").trim();
        String tokenSecret = environment.getProperty("token.secret.key");

        // Initialize the custom parser to extract claims
        JwtClaimsParser jwtClaimsParser = new JwtClaimsParser(token, tokenSecret);

        // Extract the user's email from the JWT Subject claim
        String userEmail = jwtClaimsParser.getJwtSubject();

        if (userEmail == null) {
            return null;
        }

        /*
         * Constructs the authentication token using the userEmail as the principal.
         * This ensures the security context identity aligns with controller path variables
         * and business logic requirements.
         */
        return new UsernamePasswordAuthenticationToken(
                userEmail,
                null,
                jwtClaimsParser.getUserAuthorities()
        );
    }
}