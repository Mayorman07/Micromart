package com.micromart.ConfigServer.Security;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment environment;
    @Bean
    public InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails admin = User
                .withUsername(environment.getProperty("spring.security.user.name"))
                .password(passwordEncoder.encode(environment.getProperty("spring.security.user.password")))
                .roles(environment.getProperty("spring.security.user.roles"))
                .build();

        UserDetails client = User
                .withUsername(environment.getProperty("micro-spring.security.user.name"))
                .password(passwordEncoder.encode(environment.getProperty("micro-spring.security.user.password")))
                .roles(environment.getProperty("micro-spring.security.user.roles"))
                .build();
        return new InMemoryUserDetailsManager(admin,client);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        // This rule correctly restricts the POST method on actuator endpoints to ADMINs.
                        .requestMatchers(HttpMethod.POST, "/actuator/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/encrypt").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/decrypt").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/**"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/actuator/busrefresh", "/encrypt","/decrypt"))
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}