package com.gerenciadorPet.dto.pet;

import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String name,
        String breed,
        double weight,
        double height,
        LocalDateTime bornIn,
        Long ownerId
) {}
