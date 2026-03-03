package com.micromart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micromart.exceptions.CustomAuthenticationFailureHandler;
import com.micromart.services.UserService;
import com.micromart.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Central configuration class for Spring Security.
 * Defines the security filter chain, configures stateless session management for JWTs,
 * and establishes endpoint-level authorization rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurity {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;
    private final CustomAuthenticationFailureHandler failureHandler;

    /**
     * Constructs the WebSecurity configuration with required dependencies.
     *
     * @param environment           Application environment for accessing security properties.
     * @param userService           User details service for authentication lookups.
     * @param bCryptPasswordEncoder Encoder for securely hashing and verifying passwords.
     * @param objectMapper          JSON mapper for handling authentication requests/responses.
     * @param jwtUtils              Utility for generating and validating JSON Web Tokens.
     * @param failureHandler        Custom handler for managing authentication exceptions.
     */
    public WebSecurity(Environment environment,
                       UserService userService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       ObjectMapper objectMapper,
                       JwtUtils jwtUtils,
                       CustomAuthenticationFailureHandler failureHandler) {
        this.environment = environment;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.jwtUtils = jwtUtils;
        this.failureHandler = failureHandler;
    }

    /**
     * Exposes the AuthenticationManager as a Spring Bean to be utilized across the application.
     *
     * @param authenticationConfiguration The exported authentication configuration.
     * @return The configured AuthenticationManager.
     * @throws Exception If the authentication manager cannot be built.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Constructs the primary SecurityFilterChain defining the HTTP security rules.
     *
     * @param http The HttpSecurity builder.
     * @return The fully configured SecurityFilterChain.
     * @throws Exception If an error occurs during filter chain configuration.
     */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Configure custom authentication filter for login processing
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager, objectMapper, jwtUtils);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                // Setup and Status Endpoints
                .requestMatchers("/api/setup/create-admin").permitAll()
                .requestMatchers(HttpMethod.GET, "/status/check").permitAll()

                // Authentication and User Registration
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/**").permitAll()
                .requestMatchers("/password-reset/**").permitAll()
                .requestMatchers("/verification_success.html", "/verification_failure.html").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
                .requestMatchers(HttpMethod.POST, "/users/refresh-token").permitAll()

                // Actuator and Database Consoles
                .requestMatchers(new AntPathRequestMatcher("/actuator/**", HttpMethod.GET.name())).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                // Require authentication for all other requests
                .anyRequest().authenticated()
        );

        // Register custom filters
        http.addFilter(new AuthorizationFilter(authenticationManager, environment))
                .addFilter(authenticationFilter);

        // Configure stateless sessions for JWT architecture
        http.authenticationManager(authenticationManager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Disable frame options to allow H2 console rendering
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}