package com.gerenciadorPet.dto.user;

import com.gerenciadorPet.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Positive(message = "Id deve ser maior que zero")
        Long id,

        @NotBlank(message = "Username é obrigatório")
        @Size(max = 100, message = "Username deve ter no máximo 100 caracteres")
        String username,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(max = 100, message = "Senha deve ter no máximo 100 caracteres")
        String password,

        @NotNull(message = "Perfil é obrigatório")
        UserRole admin,

        @NotNull(message = "Status ativo é obrigatório")
        Boolean active
) {}
