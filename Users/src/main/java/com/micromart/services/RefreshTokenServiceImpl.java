package com.micromart.services;

import com.micromart.entities.RefreshToken;
import com.micromart.entities.User;
import com.micromart.exceptions.ExpiredTokenException;
import com.micromart.exceptions.NotFoundException;
import com.micromart.models.requests.TokenRefreshRequest;
import com.micromart.models.responses.TokenRefreshResponse;
import com.micromart.repositories.RefreshTokenRepository;
import com.micromart.repositories.UserRepository;
import com.micromart.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    @Value("${token.refresh.expiration.time:1296000000}")
    private Long refreshTokenDurationMs;
    @Override
    @Transactional
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
    @Transactional
    public TokenRefreshResponse generateNewAccessToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .flatMap(role -> role.getAuthorities().stream())
                            .map(auth -> new SimpleGrantedAuthority(auth.getName()))
                            .collect(Collectors.toList());
                    user.getRoles().forEach(role ->
                            authorities.add(new SimpleGrantedAuthority(role.getName()))
                    );

                    String newAccessToken = jwtUtils.generateAccessToken(user.getEmail(), authorities);
                    return new TokenRefreshResponse(newAccessToken, requestRefreshToken, "Bearer");
                })
                .orElseThrow(() -> new NotFoundException("Refresh token is not in database!"));
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

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
