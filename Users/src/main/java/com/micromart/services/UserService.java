package com.micromart.services;

import com.micromart.models.data.UserDto;
import com.micromart.models.data.UserProfileDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    UserDto updateUser(UserDto userDetails);
    UserProfileDto viewProfile(String email);
    void updateLastLoggedIn(String userId);
    void assignManagerRole(String userId);
    UserDto getUserDetailsByEmail(String email);
    boolean requestPasswordReset(String email);
    boolean performPasswordReset(String token, String newPassword);
    boolean verifyUser(String token);
    int sendWeMissedYouEmails();
    void deleteUser(String email);
}
