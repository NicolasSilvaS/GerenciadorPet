package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.user.UpdateUserRequest;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.dto.user.UserResponse;
import com.gerenciadorPet.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUser(id));
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(request));
    }

    @PutMapping("/{id}/full")
    public ResponseEntity<UserResponse> updateFull(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        if (request.id() == null || !request.id().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.updateUser(request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }
}
