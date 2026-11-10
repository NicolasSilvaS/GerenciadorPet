package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.auth.*;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.dto.user.UserResponse;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import com.gerenciadorPet.utils.BCryptEncoder;
import com.gerenciadorPet.utils.EmailSender;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailSender emailSender;

    public UserLoginResponse loginUser(UserLoginRequest userLogin) {
        User user = userRepository.findByUsername(userLogin.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.encoder().matches(userLogin.password(), user.getPassword()) ||
                !Boolean.TRUE.equals(user.getActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        return new UserLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAdmin(),
                user.getActive(),
                jwtService.generateAccessToken(user),
                "Bearer",
                jwtService.getAccessTokenLifetimeSeconds()
        );
    }

    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest userRegister) {
        if (userRepository.existsByUsername(userRegister.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já está em uso");
        }
        if (userRepository.existsByEmail(userRegister.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já está em uso");
        }

        User user = User.builder()
                .username(userRegister.username())
                .email(userRegister.email())
                .password(passwordEncoder.encoder().encode(userRegister.password()))
                .admin(UserRole.STANDARD)
                .active(true)
                .build();

        userRepository.save(user);
        return new UserRegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getAdmin(),
                user.getActive()
        );
    }

    @Transactional
    public UserResponse updateUser(UserRequest updatedUser, String authenticatedUsername) {
        User user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (updatedUser.id() == null || !Objects.equals(user.getId(), updatedUser.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido alterar outro usuário");
        }
        if (userRepository.existsByUsernameAndIdNot(updatedUser.username(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já está em uso");
        }
        if (userRepository.existsByEmail(updatedUser.email()) && !user.getEmail().equals(updatedUser.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já está em uso");
        }

        user.setUsername(updatedUser.username());
        user.setEmail(updatedUser.email());
        user.setPassword(passwordEncoder.encoder().encode(updatedUser.password()));
        user.setPasswordResetTokenId(null);
        return toResponse(userRepository.save(user));
    }

    public void requestPasswordRecovery(String email) {
        userRepository.findByEmail(email)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .ifPresent(user -> {
                    String token = jwtService.generatePasswordResetToken(user);
                    Claims claims = jwtService.parsePasswordResetToken(token);
                    user.setPasswordResetTokenId(claims.getId());
                    userRepository.saveAndFlush(user);
                    emailSender.sendPasswordResetEmail(user, token);
                });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Claims claims = jwtService.parsePasswordResetToken(token);
        long userId;
        try {
            userId = Long.parseLong(claims.get("userId", String.class));
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado"));
        if (!Boolean.TRUE.equals(user.getActive()) ||
                !Objects.equals(user.getPasswordResetTokenId(), claims.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado");
        }

        user.setPassword(passwordEncoder.encoder().encode(newPassword));
        user.setPasswordResetTokenId(null);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAdmin(),
                user.getActive()
        );
    }
}
