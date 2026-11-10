package com.gerenciadorPet.controller;

import com.gerenciadorPet.dto.auth.UserLoginRequest;
import com.gerenciadorPet.dto.auth.UserLoginResponse;
import com.gerenciadorPet.dto.auth.MessageResponse;
import com.gerenciadorPet.dto.auth.PasswordRecoveryRequest;
import com.gerenciadorPet.dto.auth.PasswordResetRequest;
import com.gerenciadorPet.dto.auth.UserRegisterRequest;
import com.gerenciadorPet.dto.auth.UserRegisterResponse;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.dto.user.UserResponse;
import com.gerenciadorPet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody PasswordRecoveryRequest request) {
        userService.requestPasswordRecovery(request.email());
        return ResponseEntity.ok(new MessageResponse(
                "Se houver uma conta ativa associada ao email, você receberá as instruções para redefinir a senha."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso."));
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(
            @Valid @RequestBody UserRequest request,
            @AuthenticationPrincipal String username
    ) {
        return ResponseEntity.ok(userService.updateUser(request, username));
    }
}
