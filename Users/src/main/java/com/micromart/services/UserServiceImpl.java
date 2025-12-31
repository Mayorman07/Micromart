package com.micromart.services;

import com.micromart.constants.Status;
import com.micromart.entities.User;
import com.micromart.exceptions.ConflictException;
import com.micromart.models.data.CustomUserDetails;
import com.micromart.models.data.UserDto;
import com.micromart.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Override
    public UserDto createUser(UserDto userDetails) {

        if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            logger.info("User with email {} already exists!", userDetails.getEmail());
            throw new ConflictException("Existing user!");
        }
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetails.setStatus(Status.INACTIVE);
        User userToBeCreated = modelMapper.map(userDetails, User.class);
        String verificationToken = UUID.randomUUID().toString();
        userToBeCreated.setVerificationToken(verificationToken);
        User savedUser = userRepository.save(userToBeCreated);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public void updateUser() {

    }

    @Override
    public void viewProfile() {

    }

    @Override
    public void updateLastLoggedIn(String userId) {

    }

    @Override
    public void viewUserDetails() {

    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        return null;
    }

    @Override
    public void assignManagerRole() {

    }

    @Override
    public void createInitialAdmin() {

    }

    @Override
    public boolean requestPasswordReset(String email) {
        return false;
    }

    @Override
    public boolean performPasswordReset(String token, String newPassword) {
        return false;
    }

    @Override
    public boolean verifyUser(String token) {
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

        return new CustomUserDetails(userToBeLoggedIn.get().getEmail(), userToBeLoggedIn.get().getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
//
//        if (userToBeLoggedIn.get().getStatus() != Status.ACTIVE) {
//            throw new DisabledException("User account is not active. Status: " + userToBeLoggedIn.get().getStatus());
//        }
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        Collection<Role> roles = userToBeLoggedIn.get().getRoles();
//
//        roles.forEach((role) ->{
//            authorities.add(new SimpleGrantedAuthority((role.getName())));
//            Collection<Authority> authorityEntities = role.getAuthorities();
//
//            authorityEntities.forEach((authorityEntity -> {
//                authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
//            }));
//        });
//
//        //enabled after password can be false until the user successfully verifys their email
//        return new CustomUserDetails(employeeToBeLoggedIn.get().getEmail(), employeeToBeLoggedIn.get().getEncryptedPassword(),
//                true, true, true,true,
//                authorities,employeeToBeLoggedIn.get().getUserId(),
//                employeeToBeLoggedIn.get().getDepartment());
//    }
    }
}