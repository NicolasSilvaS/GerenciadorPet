package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.pet.PetRequest;
import com.gerenciadorPet.model.Pet;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.repository.PetRepository;
import com.gerenciadorPet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetAndMetricServiceTest {

    @Mock PetRepository petRepository;
    @Mock UserRepository userRepository;
    @Mock MetricService metricService;
    @InjectMocks PetService petService;

    @Test
    void shouldCreatePetAndRecordFirstMetric() {
        User owner = User.builder().id(3).username("ana").active(true).build();
        PetRequest request = new PetRequest("Luna", "Vira-lata", 8.4, 42.0, 3L, LocalDateTime.of(2024, 3, 10, 12, 0));

        when(userRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(12);
            return pet;
        });

        var response = petService.createPet(request);

        assertEquals("Luna", response.name());
        assertEquals(8.4, response.weight());
        verify(metricService).recordMeasurement(any(Pet.class));
    }

    @Test
    void shouldUpdatePetName() {
        User owner = User.builder().id(3).username("ana").active(true).build();
        Pet pet = Pet.builder().id(12).name("Antes").breed("SRD").weight(8).height(40).bornIn(LocalDateTime.now()).owner(owner).build();

        when(petRepository.findById(12L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = petService.updatePet(new com.gerenciadorPet.dto.pet.PetUpdateRequest(12L, "Depois", "SRD", 8.5, 41, LocalDateTime.now()));

        assertEquals("Depois", response.name());
        verify(metricService).recordMeasurement(pet);
    }
}
