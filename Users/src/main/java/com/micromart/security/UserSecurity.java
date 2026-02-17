package com.micromart.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean canViewProfile(String email, Authentication authentication) {
        String currentAuthenticatedEmail = (String) authentication.getPrincipal();

        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN") ||
                        ga.getAuthority().equals("user:READ"));

        boolean isOwner = email.equalsIgnoreCase(currentAuthenticatedEmail);

        return isPrivileged || isOwner;
    }
}