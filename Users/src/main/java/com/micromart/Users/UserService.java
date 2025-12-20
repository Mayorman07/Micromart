package com.micromart.Users;

import java.util.List;

public interface UserService {
    void createUser();
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
