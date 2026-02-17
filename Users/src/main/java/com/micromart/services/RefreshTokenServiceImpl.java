package com.micromart.services;

import com.micromart.entities.RefreshToken;
import com.micromart.entities.User;
import com.micromart.exceptions.ExpiredTokenException;
import com.micromart.exceptions.NotFoundException;
import com.micromart.repositories.RefreshTokenRepository;
import com.micromart.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${token.refresh.expiration.time:2592000000}")
    private Long refreshTokenDurationMs;
    @Override
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ExpiredTokenException("Expired refresh token. Kindly sign in again");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteTokenByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
