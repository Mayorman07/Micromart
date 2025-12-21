package com.micromart.services;

import com.micromart.models.data.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    void updateUser();
    void  viewProfile();
    void updateLastLoggedIn(String userId);
    void viewUserDetails();
    UserDto getUserDetailsByEmail(String email);

    void assignManagerRole();
    void createInitialAdmin();
    boolean requestPasswordReset(String email);
    boolean performPasswordReset(String token, String newPassword);
    boolean verifyUser(String token);
    int sendWeMissedYouEmails();
}
