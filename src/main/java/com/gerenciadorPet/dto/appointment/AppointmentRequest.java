package com.gerenciadorPet.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
        String title,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 300, message = "Descrição deve ter no máximo 300 caracteres")
        String description,

        @NotNull(message = "Data do agendamento é obrigatória")
        LocalDateTime date,

        @NotNull(message = "Id do pet é obrigatório")
        @Positive(message = "Id do pet deve ser maior que zero")
        Long petId
) {}
