package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.appointment.AppointmentRequest;
import com.gerenciadorPet.dto.appointment.AppointmentResponse;
import com.gerenciadorPet.dto.appointment.AppointmentUpdateRequest;
import com.gerenciadorPet.model.Appointment;
import com.gerenciadorPet.model.Pet;
import com.gerenciadorPet.repository.AppointmentRepository;
import com.gerenciadorPet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        Appointment appointment = Appointment.builder()
                .title(request.title())
                .description(request.description())
                .date(request.date())
                .pet(pet)
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long id) {
        return toResponse(findAppointment(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPet(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }

        return appointmentRepository.findByPet_IdOrderByDateAsc(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse updateAppointment(AppointmentUpdateRequest request) {
        Appointment appointment = findAppointment(request.id());
        appointment.setTitle(request.title());
        appointment.setDescription(request.description());
        appointment.setDate(request.date());
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.delete(findAppointment(id));
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getDate(),
                appointment.getPet().getId()
        );
    }
}
