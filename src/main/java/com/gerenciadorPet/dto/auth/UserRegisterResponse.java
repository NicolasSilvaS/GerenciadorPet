package com.gerenciadorPet.dto.auth;

import com.gerenciadorPet.model.UserRole;

public record UserRegisterResponse(
        Long id,
        String email,
        String username,
        UserRole admin,
        Boolean active
) {}