package com.micromart.services;

import com.micromart.entities.RefreshToken;
import com.micromart.entities.User;
import com.micromart.models.requests.TokenRefreshRequest;
import com.micromart.models.responses.TokenRefreshResponse;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteTokenByUser(User user);
    Optional<RefreshToken> findByToken(String token);
    TokenRefreshResponse generateNewAccessToken(TokenRefreshRequest request);
}
