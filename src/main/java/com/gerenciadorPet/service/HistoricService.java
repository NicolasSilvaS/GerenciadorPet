package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.historic.HistoricRequest;
import com.gerenciadorPet.dto.historic.HistoricResponse;
import com.gerenciadorPet.model.Historic;
import com.gerenciadorPet.model.Pet;
import com.gerenciadorPet.repository.HistoricRepository;
import com.gerenciadorPet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricService {

    private final HistoricRepository historicRepository;
    private final PetRepository petRepository;

    @Transactional
    public HistoricResponse createHistoric(HistoricRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        Historic historic = Historic.builder()
                .description(request.description())
                .type(request.type())
                .createdAt(LocalDateTime.now())
                .pet(pet)
                .build();

        return toResponse(historicRepository.save(historic));
    }

    @Transactional(readOnly = true)
    public HistoricResponse getHistoric(Long id) {
        return toResponse(findHistoric(id));
    }

    @Transactional(readOnly = true)
    public List<HistoricResponse> getHistoricsByPet(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }

        return historicRepository.findByPet_IdOrderByCreatedAtDesc(petId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteHistoric(Long id) {
        historicRepository.delete(findHistoric(id));
    }

    private Historic findHistoric(Long id) {
        return historicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro do histórico não encontrado"));
    }

    private HistoricResponse toResponse(Historic historic) {
        return new HistoricResponse(
                historic.getId(),
                historic.getDescription(),
                historic.getCreatedAt(),
                historic.getType(),
                historic.getPet().getId()
        );
    }
}
