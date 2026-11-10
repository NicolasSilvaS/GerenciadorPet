package com.gerenciadorPet.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
        String email,

        @NotBlank(message = "Username é obrigatório")
        @Size(max = 100, message = "Username deve ter no máximo 100 caracteres")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(max = 100, message = "Senha deve ter no máximo 100 caracteres")
        String password
) {}
