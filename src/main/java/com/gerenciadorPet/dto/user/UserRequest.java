package com.gerenciadorPet.dto.user;

import com.gerenciadorPet.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "Id não pode ser nulo")
        Long id,

        @NotBlank(message = "Username é obrigatório")
        String username,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        @NotNull(message = "Admin não pode ser nulo")
        UserRole admin,

        @NotNull(message = "active não pode ser nulo")
        Boolean active
) {

}
