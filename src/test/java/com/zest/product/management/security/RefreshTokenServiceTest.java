package com.zest.product.management.security;

import com.zest.product.management.entity.RefreshToken;
import com.zest.product.management.entity.User;
import com.zest.product.management.repository.RefreshTokenRepository;
import com.zest.product.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 600000L);
        user = User.builder().id(1L).username("testuser").build();
        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
    }

    @Test
    void findByToken_ShouldReturnToken() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> result = refreshTokenService.findByToken("token");
        assertTrue(result.isPresent());
    }

    @Test
    void createRefreshToken_ShouldUpsertToken() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(refreshTokenRepository.saveAndFlush(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(1L);

        assertNotNull(result);
        verify(refreshTokenRepository).saveAndFlush(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_WhenValid_ShouldReturnToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);
        assertEquals(refreshToken, result);
    }

    @Test
    void verifyExpiration_WhenExpired_ShouldThrowException() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));
        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository).delete(refreshToken);
    }
}
