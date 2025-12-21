package com.micromart.services;

import com.micromart.constants.Status;
import com.micromart.entities.User;
import com.micromart.exceptions.ConflictException;
import com.micromart.models.data.UserDto;
import com.micromart.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

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
        return null;
    }
}
