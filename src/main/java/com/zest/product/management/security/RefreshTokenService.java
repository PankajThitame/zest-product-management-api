package com.zest.product.management.security;

import com.zest.product.management.entity.RefreshToken;
import com.zest.product.management.entity.User;
import com.zest.product.management.exception.TokenRefreshException;
import com.zest.product.management.repository.RefreshTokenRepository;
import com.zest.product.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to handle Refresh Token generation, validation, and rotation.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${zest.app.jwtRefreshExpirationMs:604800000}") // 7 days
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // Check if user already has a refresh token (Upsert/Rotation)
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.saveAndFlush(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    refreshTokenRepository.findByUser(user)
                            .ifPresent(refreshTokenRepository::delete);
                    return 1;
                }).orElse(0);
    }
}
