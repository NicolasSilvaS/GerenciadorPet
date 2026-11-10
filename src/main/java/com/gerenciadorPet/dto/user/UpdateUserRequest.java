package com.gerenciadorPet.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotNull(message = "Id é obrigatório")
        @Positive(message = "Id deve ser maior que zero")
        Long id,

        @NotBlank(message = "Username é obrigatório")
        @Size(max = 100, message = "Username deve ter no máximo 100 caracteres")
        String username,

        @NotNull(message = "Admin é obrigatório")
        Boolean admin,

        @NotNull(message = "Status ativo é obrigatório")
        Boolean active
) {}
