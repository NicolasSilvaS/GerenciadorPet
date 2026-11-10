package com.gerenciadorPet.dto.auth;

import com.gerenciadorPet.model.UserRole;

public record UserLoginResponse(
        Long id,
        String username,
        String email,
        UserRole admin,
        Boolean active,
        String accessToken,
        String tokenType,
        long expiresIn
) {}