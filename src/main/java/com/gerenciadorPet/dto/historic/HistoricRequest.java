package com.gerenciadorPet.dto.historic;

import com.gerenciadorPet.model.HistoricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HistoricRequest(
        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 300, message = "Descrição deve ter no máximo 300 caracteres")
        String description,

        @NotNull(message = "Tipo do registro é obrigatório")
        HistoricType type,

        @NotNull(message = "Id do pet é obrigatório")
        @Positive(message = "Id do pet deve ser maior que zero")
        Long petId
) {}
