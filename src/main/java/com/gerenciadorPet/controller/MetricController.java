package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.metric.MetricResponse;
import com.gerenciadorPet.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/pets/{petId}/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    @GetMapping
    public ResponseEntity<List<MetricResponse>> getMetrics(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate dateInitial,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate dateFinal
    ) {
        return ResponseEntity.ok(metricService.getMetrics(petId, dateInitial, dateFinal));
    }
}
