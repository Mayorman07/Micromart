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
    boolean requestPasswordReset(String email);
    void deactivateUser(String email);
    boolean performPasswordReset(String token, String newPassword);
    boolean verifyUser(String token);
    void deleteUser(String email);
}
