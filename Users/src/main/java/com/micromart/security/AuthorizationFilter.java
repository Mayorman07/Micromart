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

@Component
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Environment environment;

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

        // 🛡️ Sets the verified Email and Authorities into the Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String headerName = environment.getProperty("authorization.token.header.name");
        String headerPrefix = environment.getProperty("authorization.token.header.prefix");

        String authorizationHeader = req.getHeader(headerName);

        if (authorizationHeader == null) {
            return null;
        }

        // ✂️ Strip the 'Bearer ' prefix to get the raw token
        String token = authorizationHeader.replace(Objects.requireNonNull(headerPrefix), "").trim();
        String tokenSecret = environment.getProperty("token.secret.key");

        // 🎹 Initialize our custom parser
        JwtClaimsParser jwtClaimsParser = new JwtClaimsParser(token, tokenSecret);

        // 🎯 CRITICAL: This now pulls the EMAIL from the JWT Subject
        String userEmail = jwtClaimsParser.getJwtSubject();

        if (userEmail == null) {
            return null;
        }

        /**
         * 🏆 THE WINNING MOVE:
         * We pass userEmail as the 'Principal' (1st argument).
         * This aligns the Token Identity with the Controller's @PathVariable email.
         */
        return new UsernamePasswordAuthenticationToken(
                userEmail,
                null,
                jwtClaimsParser.getUserAuthorities()
        );
    }
}