package com.gerenciadorPet.repository;

import com.gerenciadorPet.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPet_IdOrderByDateAsc(Long petId);
}
