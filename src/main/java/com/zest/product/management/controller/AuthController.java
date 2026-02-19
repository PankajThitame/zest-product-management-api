package com.zest.product.management.controller;

import com.zest.product.management.dto.*;
import com.zest.product.management.entity.RefreshToken;
import com.zest.product.management.exception.TokenRefreshException;
import com.zest.product.management.security.JwtUtils;
import com.zest.product.management.security.RefreshTokenService;
import com.zest.product.management.service.AuthService;
import com.zest.product.management.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Authentication operations (Login, Register, Refresh Token).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for User Registration, Login and Token Refresh")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Login user and return JWT access + refresh tokens")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .ok(ApiResponse.success(authService.authenticateUser(loginRequest), "User logged in successfully"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user with roles")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "User registered successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token using dynamic refresh token rotation")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Create new access token
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    // Rotate the refresh token (Delete old, create new)
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                    return ResponseEntity.ok(ApiResponse.success(
                            new TokenRefreshResponse(token, newRefreshToken.getToken()),
                            "Token refreshed successfully (Rotation applied)"));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}
