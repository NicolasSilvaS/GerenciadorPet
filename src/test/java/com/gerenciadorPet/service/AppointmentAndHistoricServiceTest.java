package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.appointment.AppointmentRequest;
import com.gerenciadorPet.dto.historic.HistoricRequest;
import com.gerenciadorPet.model.*;
import com.gerenciadorPet.repository.AppointmentRepository;
import com.gerenciadorPet.repository.HistoricRepository;
import com.gerenciadorPet.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentAndHistoricServiceTest {

    @Mock PetRepository petRepository;
    @Mock AppointmentRepository appointmentRepository;
    @Mock HistoricRepository historicRepository;

    @Test
    void shouldCreateAppointmentForPet() {
        Pet pet = Pet.builder().id(4).name("Nina").build();
        when(petRepository.findById(4L)).thenReturn(Optional.of(pet));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(8);
            return appointment;
        });

        AppointmentService service = new AppointmentService(appointmentRepository, petRepository);
        var response = service.createAppointment(new AppointmentRequest("Consulta", "Vacinação anual", LocalDateTime.now().plusDays(3), 4L));

        assertEquals("Consulta", response.title());
        assertEquals(4L, response.petId());
    }

    @Test
    void shouldCreateHistoricMemoryForPet() {
        Pet pet = Pet.builder().id(4).name("Nina").build();
        when(petRepository.findById(4L)).thenReturn(Optional.of(pet));
        when(historicRepository.save(any(Historic.class))).thenAnswer(invocation -> {
            Historic historic = invocation.getArgument(0);
            historic.setId(9);
            return historic;
        });

        HistoricService service = new HistoricService(historicRepository, petRepository);
        var response = service.createHistoric(new HistoricRequest("Primeiro passeio", HistoricType.IDENITITY, 4L));

        assertEquals("Primeiro passeio", response.description());
        assertEquals(HistoricType.IDENITITY, response.type());
    }
}
