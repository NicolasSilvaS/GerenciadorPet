package com.gerenciadorPet.dto.metric;

import java.time.LocalDateTime;

public record MetricResponse(
        LocalDateTime measuredAt,
        double weight,
        double height
) {}
