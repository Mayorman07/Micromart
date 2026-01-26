package com.micromart.services;

import com.micromart.constants.AddressType;
import com.micromart.constants.Status;
import com.micromart.entities.Address;
import com.micromart.entities.Authority;
import com.micromart.entities.Role;
import com.micromart.entities.User;
import com.micromart.exceptions.ConflictException;
import com.micromart.exceptions.NotFoundException;
import com.micromart.messaging.MessagePublisher;
import com.micromart.messaging.PasswordResetRequestEvent;
import com.micromart.models.data.CustomUserDetails;
import com.micromart.models.data.PasswordResetEventDto;
import com.micromart.models.data.UserCreatedEventDto;
import com.micromart.models.data.UserDto;
import com.micromart.models.data.UserProfileDto;
import com.micromart.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.micromart.repositories.RoleRepository;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MessagePublisher messagePublisher;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    @Transactional
    public UserDto createUser(UserDto userDetails) {

        if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            logger.info("User with email {} already exists!, ", userDetails.getEmail());
            throw new ConflictException("Existing user!");
        }
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetails.setStatus(Status.INACTIVE);
        User userToBeCreated = modelMapper.map(userDetails, User.class);
        if (userDetails.getAddress() != null) {
            Address addressEntity = modelMapper.map(userDetails.getAddress(), Address.class);
            addressEntity.setUser(userToBeCreated);
            addressEntity.setAddressId(UUID.randomUUID().toString());
            addressEntity.setType(AddressType.SHIPPING);
            userToBeCreated.setAddresses(List.of(addressEntity));
        }
        String verificationToken = UUID.randomUUID().toString();
        userToBeCreated.setVerificationToken(verificationToken);
        User savedUser = userRepository.save(userToBeCreated);
        publishUserCreatedEvent(savedUser, verificationToken);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDetails) {
        User existingUser = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> {
                    logger.info("User with email {} not found for update!", userDetails.getEmail());
                    return new NotFoundException("User not found!");
                });
        modelMapper.map(userDetails,existingUser);
        User userToBeUpdated = userRepository.save(existingUser);
        return modelMapper.map(userToBeUpdated,UserDto.class);
    }

    @Override
    public UserProfileDto viewProfile(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.info("User with email {} not found for viewing!", email);
                    return new NotFoundException("User not found!");
                });

        return modelMapper.map(existingUser, UserProfileDto.class);
    }

    @Override
    public void updateLastLoggedIn(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        user.setLastLoggedIn(new Date());
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void assignManagerRole(String userId) {
        User employee = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: " + userId));
        Role managerRole = roleRepository.findByName("ROLE_MANAGER");
        if (managerRole == null) {
            throw new RuntimeException("Error: ROLE_MANAGER not found.");
        }
        Collection<Role> userRoles = employee.getRoles();
        if (!userRoles.contains(managerRole)) {
            userRoles.add(managerRole);
            employee.setRoles(userRoles);
            userRepository.save(employee);
        }
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.info("User with email {} not found for deleting!", email);
                    return new NotFoundException("User not found!");
                });
        userRepository.delete(existingUser);
        logger.info("The user with the email has been deleted");
    }

    @Override
    public boolean requestPasswordReset(String email) {
        PasswordResetRequestEvent event = new PasswordResetRequestEvent(email);
        messagePublisher.sendPasswordResetAttempt(event);
        logger.info("Queued password reset attempt for potential user: {}", email);
        return true;
    }


    @Override
    public boolean performPasswordReset(String token, String newPassword) {
        Optional<User> employeeOptional = userRepository.findByPasswordResetToken(token);

        if (employeeOptional.isEmpty()) {
            logger.warn("Password reset attempted with an invalid token.");
            return false;
        }
        User user = employeeOptional.get();
        // 2. Check if the token has expired
        if (user.getPasswordResetTokenExpiryDate().before(new Date())) {
            logger.warn("Expired password reset token used for user: {}", user.getEmail());
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiryDate(null);
            userRepository.save(user);
            return false;
        }
        // 3. If the token is valid, update the password
        user.setEncryptedPassword(passwordEncoder.encode(newPassword));

        // 4. CRITICAL: Invalidate the token so it cannot be used again
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiryDate(null);

        userRepository.save(user);

        logger.info("Password successfully reset for user: {}", user.getEmail());
        return true;
    }

    @Override
    public void deactivateUser(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.info("User with email {} not found for deactivating!", email);
                    return new NotFoundException("User not found!");
                });
        existingUser.setStatus(Status.DEACTIVATED);
        User userToBeDeactivate = modelMapper.map(existingUser,User.class);
        userRepository.save(userToBeDeactivate);
    }


    @Override
    public boolean verifyUser(String token) {
        Optional<User> userWithVerificationToken = userRepository.findByVerificationToken(token);
        if (userWithVerificationToken.isPresent()) {
            User employee = userWithVerificationToken.get();
            employee.setStatus(Status.ACTIVE);
            employee.setVerificationToken(null);
            userRepository.save(employee);
            return true;
        }
        return false;
    }

    @Override
    public int sendWeMissedYouEmails() {
        return 0;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userToBeLoggedIn = userRepository.findByEmail(username);
        if (userToBeLoggedIn.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        if (userToBeLoggedIn.get().getStatus() != Status.ACTIVE) {
            throw new DisabledException("User account is not active. Status: " + userToBeLoggedIn.get().getStatus());
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Collection<Role> roles = userToBeLoggedIn.get().getRoles();

        roles.forEach((role) ->{
            authorities.add(new SimpleGrantedAuthority((role.getName())));
            Collection<Authority> authorityEntities = role.getAuthorities();

            authorityEntities.forEach((authorityEntity -> {
                authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
            }));
        });
//        //enabled after password can be false until the user successfully verifies their email
        return new CustomUserDetails(userToBeLoggedIn.get().getEmail(), userToBeLoggedIn.get().getEncryptedPassword(),
                true, true, true,true,
                authorities,userToBeLoggedIn.get().getUserId(),
                userToBeLoggedIn.get().getEmail());
    }

    private void publishUserCreatedEvent(User savedEmployee, String verificationToken) {
        UserCreatedEventDto eventDto = new UserCreatedEventDto(
                savedEmployee.getFirstName(),
                savedEmployee.getEmail(),
                verificationToken,
                savedEmployee.getMobileNumber(),
                "USER_CREATED"
        );
        messagePublisher.sendUserCreatedEvent(eventDto);
        logger.info("Published User Created event for token: {}", verificationToken);
        logger.info("Published User Created event for email: {}", savedEmployee.getEmail());
    }
}
