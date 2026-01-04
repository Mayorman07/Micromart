package com.micromart;

import com.micromart.constants.Roles;
import com.micromart.constants.Status;
import com.micromart.entities.Authority;
import com.micromart.entities.Role;
import com.micromart.entities.User;
import com.micromart.repositories.AuthorityRepository;
import com.micromart.repositories.RoleRepository;
import com.micromart.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitialUsersSetup {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository usersRepository;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("🚦 Application Ready - Checking/Seeding Admin User...");

        // Products
        Authority productRead = createAuthority("product:READ");
        Authority productWrite = createAuthority("product:WRITE");

        // Users (Management)
        Authority userRead = createAuthority("user:READ");
        Authority userWrite = createAuthority("user:WRITE");

        createRole(Roles.ROLE_USER.name(), Arrays.asList(productRead));
        createRole(Roles.ROLE_MANAGER.name(), Arrays.asList(productRead, productWrite, userRead));
        Role roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(productRead, productWrite, userRead, userWrite));

        Optional<User> storedAdminUser = usersRepository.findByEmail(adminEmail);

        if (storedAdminUser.isEmpty()) {
            User adminUser = new User();
            adminUser.setFirstName("Mayor");
            adminUser.setLastName("Olajide");
            adminUser.setEmail(adminEmail);
            adminUser.setUserId(String.valueOf(UUID.randomUUID()));
            adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode(adminPassword));
            adminUser.setRoles(Collections.singletonList(roleAdmin));
            adminUser.setStatus(Status.ACTIVE);
            usersRepository.save(adminUser);
            logger.info("✅ Admin User Created Successfully!");
        } else {
            logger.info("ℹ️ Admin User already exists. Skipping.");
        }
    }
    private Authority createAuthority(String name) {
        Authority authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new Authority(name);
            authorityRepository.save(authority);
        }
        return authority;
    }
    private Role createRole(String name, Collection<Authority> authorities) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name, authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
