package com.gerenciadorPet.dto.appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String title,
        String description,
        LocalDateTime date,
        Long petId
) {}
