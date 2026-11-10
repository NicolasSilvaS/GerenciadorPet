package com.gerenciadorPet.repository;

import com.gerenciadorPet.model.Historic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricRepository extends JpaRepository<Historic, Long> {
    List<Historic> findByPet_IdOrderByCreatedAtDesc(Long petId);
}
