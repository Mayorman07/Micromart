package com.micromart.Users.services;

import com.micromart.Users.models.data.UserDto;

public interface UserService{
    UserDto createUser(UserDto userDetails);
    void updateUser();
    void  viewProfile();
    void updateLastLoggedIn();
    void viewUserDetails();
    void assignManagerRole();
    void createInitialAdmin();
    boolean requestPasswordReset(String email);
    boolean performPasswordReset(String token, String newPassword);
    boolean verifyUser(String token);
    int sendWeMissedYouEmails();
}
