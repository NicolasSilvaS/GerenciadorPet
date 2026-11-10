package com.gerenciadorPet.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PetUpdateRequest(
        @NotNull(message = "Id é obrigatório")
        @Positive(message = "Id deve ser maior que zero")
        Long id,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,

        @NotBlank(message = "Raça é obrigatória")
        @Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
        String breed,

        @Positive(message = "Peso deve ser maior que zero")
        double weight,

        @Positive(message = "Altura deve ser maior que zero")
        double height,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDateTime bornIn
) {}
