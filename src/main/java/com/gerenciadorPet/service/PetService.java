package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.pet.PetRequest;
import com.gerenciadorPet.dto.pet.PetResponse;
import com.gerenciadorPet.dto.pet.PetUpdateRequest;
import com.gerenciadorPet.model.Pet;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.repository.PetRepository;
import com.gerenciadorPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final MetricService metricService;

    @Transactional
    public PetResponse createPet(PetRequest request) {
        User owner = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Pet pet = Pet.builder()
                .name(request.name())
                .breed(request.breed())
                .weight(request.weight())
                .height(request.height())
                .bornIn(request.bornIn())
                .owner(owner)
                .build();

        Pet savedPet = petRepository.save(pet);
        metricService.recordMeasurement(savedPet);
        return toResponse(savedPet);
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(Long id) {
        return toResponse(findPet(id));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> getPetsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        return petRepository.findByOwner_Id(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PetResponse updatePet(PetUpdateRequest request) {
        Pet pet = findPet(request.id());
        pet.setName(request.name());
        pet.setBreed(request.breed());
        pet.setWeight(request.weight());
        pet.setHeight(request.height());
        pet.setBornIn(request.bornIn());
        Pet savedPet = petRepository.save(pet);
        metricService.recordMeasurement(savedPet);
        return toResponse(savedPet);
    }

    @Transactional
    public void deletePet(Long id) {
        Pet pet = findPet(id);
        petRepository.delete(pet);
    }

    private Pet findPet(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));
    }

    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getWeight(),
                pet.getHeight(),
                pet.getBornIn(),
                pet.getOwner().getId()
        );
    }
}
