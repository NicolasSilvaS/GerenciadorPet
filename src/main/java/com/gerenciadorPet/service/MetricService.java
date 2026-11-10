package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.metric.MetricResponse;
import com.gerenciadorPet.model.Metric;
import com.gerenciadorPet.model.Pet;
import com.gerenciadorPet.repository.MetricRepository;
import com.gerenciadorPet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;
    private final PetRepository petRepository;

    @Transactional
    public void recordMeasurement(Pet pet) {
        Metric metric = Metric.builder()
                .pet(pet)
                .weight(pet.getWeight())
                .height(pet.getHeight())
                .measuredAt(LocalDateTime.now())
                .build();

        metricRepository.save(metric);
    }

    @Transactional(readOnly = true)
    public List<MetricResponse> getMetrics(Long petId, LocalDate dateInitial, LocalDate dateFinal) {
        if (petId == null || petId <= 0 || dateInitial == null || dateFinal == null ||
                dateInitial.isAfter(dateFinal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo de datas inválido");
        }

        if (!petRepository.existsById(petId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }

        return metricRepository.findByPet_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
                        petId,
                        dateInitial.atStartOfDay(),
                        dateFinal.atTime(LocalTime.MAX)
                ).stream()
                .map(this::toResponse)
                .toList();
    }

    private MetricResponse toResponse(Metric metric) {
        return new MetricResponse(
                metric.getMeasuredAt(),
                metric.getWeight(),
                metric.getHeight()
        );
    }
}
