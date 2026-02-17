package com.micromart.services;

import com.micromart.entities.RefreshToken;
import com.micromart.entities.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteTokenByUser(User user);
}
