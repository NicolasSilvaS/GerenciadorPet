package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.pet.PetRequest;
import com.gerenciadorPet.dto.pet.PetResponse;
import com.gerenciadorPet.dto.pet.PetUpdateRequest;
import com.gerenciadorPet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPet(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PetResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(petService.getPetsByUser(userId));
    }

    @PutMapping
    public ResponseEntity<PetResponse> update(@Valid @RequestBody PetUpdateRequest request) {
        return ResponseEntity.ok(petService.updatePet(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
