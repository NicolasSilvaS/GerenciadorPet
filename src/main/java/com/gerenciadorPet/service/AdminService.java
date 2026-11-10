package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.user.UpdateUserRequest;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.dto.user.UserResponse;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import com.gerenciadorPet.utils.BCryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest newUser) {
        if (userRepository.existsByUsername(newUser.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já está em uso");
        }

        User user = User.builder()
                .username(newUser.username())
                .email(newUser.email())
                .password(passwordEncoder.encoder().encode(newUser.password()))
                .active(newUser.active())
                .admin(newUser.admin())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(UserRequest updatedUser) {
        if (updatedUser.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id do usuário é obrigatório");
        }

        User user = findUser(updatedUser.id());
        ensureUsernameAvailable(updatedUser.username(), user.getId());

        user.setUsername(updatedUser.username());
        user.setEmail(updatedUser.email());
        user.setPassword(passwordEncoder.encoder().encode(updatedUser.password()));
        user.setAdmin(updatedUser.admin());
        user.setActive(updatedUser.active());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(UpdateUserRequest updatedUser) {
        User user = findUser(updatedUser.id());
        ensureUsernameAvailable(updatedUser.username(), user.getId());

        user.setUsername(updatedUser.username());
        user.setAdmin(updatedUser.admin() ? UserRole.ADMIN : UserRole.STANDARD);
        user.setActive(updatedUser.active());
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(findUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = findUser(id);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private void ensureUsernameAvailable(String username, Long currentUserId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já está em uso");
        }
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
