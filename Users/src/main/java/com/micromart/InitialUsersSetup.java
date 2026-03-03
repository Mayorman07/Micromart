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
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bootstraps the application with foundational security data.
 * Listens for the ApplicationReadyEvent to seed initial authorities, roles,
 * and the default administrator account into the database if they do not already exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitialUsersSetup {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    /**
     * Executes the seeding process when the application context is fully started.
     * Runs within a transactional context to ensure data integrity during setup.
     *
     * @param event The application ready event triggered by Spring Boot.
     */
    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application context ready. Executing foundational security data seeding...");

        // Define Product Authorities
        Authority productRead = createAuthority("product:READ");
        Authority productWrite = createAuthority("product:WRITE");

        // Define User Management Authorities
        Authority userRead = createAuthority("user:READ");
        Authority userWrite = createAuthority("user:WRITE");

        // Define Base Roles and map their corresponding authorities
        createRole(Roles.ROLE_USER.name(), Arrays.asList(productRead, userRead));
        createRole(Roles.ROLE_MANAGER.name(), Arrays.asList(productRead, productWrite, userRead));
        Role roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(productRead, productWrite, userRead, userWrite));

        // Seed initial administrator account if it is not present
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
            log.info("Initial administrator account successfully created.");
        } else {
            log.info("Administrator account already exists. Skipping user seed execution.");
        }
    }

    /**
     * Persists a new Authority if it does not already exist in the database.
     *
     * @param name The unique name of the authority (e.g., "product:READ").
     * @return The newly created or existing Authority entity.
     */
    private Authority createAuthority(String name) {
        Authority authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new Authority(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    /**
     * Persists a new Role mapping to a collection of Authorities if it does not already exist.
     *
     * @param name        The unique name of the role (e.g., "ROLE_ADMIN").
     * @param authorities The collection of authorities granted to this role.
     * @return The newly created or existing Role entity.
     */
    private Role createRole(String name, Collection<Authority> authorities) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name, authorities);
            roleRepository.save(role);
        }
        return role;
    }
}