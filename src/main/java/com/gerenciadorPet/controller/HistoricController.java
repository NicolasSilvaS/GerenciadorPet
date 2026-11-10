package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.historic.HistoricRequest;
import com.gerenciadorPet.dto.historic.HistoricResponse;
import com.gerenciadorPet.service.HistoricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historics")
@RequiredArgsConstructor
public class HistoricController {

    private final HistoricService historicService;

    @PostMapping
    public ResponseEntity<HistoricResponse> create(@Valid @RequestBody HistoricRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historicService.createHistoric(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(historicService.getHistoric(id));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<HistoricResponse>> getByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(historicService.getHistoricsByPet(petId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        historicService.deleteHistoric(id);
        return ResponseEntity.noContent().build();
    }
}
