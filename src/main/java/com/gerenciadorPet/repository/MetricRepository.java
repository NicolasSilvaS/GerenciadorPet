package com.gerenciadorPet.repository;

import com.gerenciadorPet.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findByPet_IdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long petId,
            LocalDateTime start,
            LocalDateTime end
    );
}
