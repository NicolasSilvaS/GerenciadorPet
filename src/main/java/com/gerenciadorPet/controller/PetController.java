package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.pet.PetRequest;
import com.gerenciadorPet.dto.pet.PetResponse;
import com.gerenciadorPet.dto.pet.PetUpdateRequest;
import com.gerenciadorPet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request, @AuthenticationPrincipal String username) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(request, username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getById(@PathVariable Long id, @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(petService.getPet(id, username));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PetResponse>> getByUser(@PathVariable Long userId, @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(petService.getPetsByUser(userId, username));
    }

    @PutMapping
    public ResponseEntity<PetResponse> update(@Valid @RequestBody PetUpdateRequest request, @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(petService.updatePet(request, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal String username) {
        petService.deletePet(id, username);
        return ResponseEntity.noContent().build();
    }
}
