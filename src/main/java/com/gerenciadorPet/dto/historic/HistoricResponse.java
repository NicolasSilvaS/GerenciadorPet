package com.gerenciadorPet.dto.historic;

import com.gerenciadorPet.model.HistoricType;

import java.time.LocalDateTime;

public record HistoricResponse(
        Long id,
        String description,
        LocalDateTime createdAt,
        HistoricType type,
        Long petId
) {}
