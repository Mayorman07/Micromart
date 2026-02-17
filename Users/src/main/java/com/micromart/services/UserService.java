package com.micromart.services;

import com.micromart.models.data.UserDto;
import com.micromart.models.data.UserProfileDto;
import com.micromart.models.requests.TokenRefreshRequest;
import com.micromart.models.responses.TokenRefreshResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    UserDto updateUser(UserDto userDetails);
    UserProfileDto viewProfile(String email);
    void updateLastLoggedIn(String userId);
    void assignManagerRole(String userId);
    void requestPasswordReset(String email);
    void deactivateUser(String email);
    String createRefreshToken(String userId);
    boolean performPasswordReset(String token, String newPassword);
    boolean verifyUser(String token);
    void deleteUser(String email);
    Page<UserDto> findAllUsers(Pageable pageable);
    TokenRefreshResponse generateNewAccessToken(TokenRefreshRequest request);
}
