package com.gerenciadorPet.dto.user;

import com.gerenciadorPet.model.UserRole;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole admin,
        Boolean active
) {}
