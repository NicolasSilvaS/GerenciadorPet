package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.appointment.AppointmentRequest;
import com.gerenciadorPet.dto.appointment.AppointmentResponse;
import com.gerenciadorPet.dto.appointment.AppointmentUpdateRequest;
import com.gerenciadorPet.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AppointmentResponse>> getByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPet(petId));
    }

    @PutMapping
    public ResponseEntity<AppointmentResponse> update(
            @Valid @RequestBody AppointmentUpdateRequest request
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointment(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
