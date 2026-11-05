package com.gerenciadorPet.service;


import com.gerenciadorPet.dto.user.*;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.gerenciadorPet.utils.BCryptEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptEncoder passwordEncoder;

    //Criar Usuarios manual
    public UserResponse createUser(UserRequest newUser) {
           User createdUser = User.builder()
                   .username(newUser.username())
                   .email(newUser.email())
                   .password(newUser.password())
                   .active(newUser.active())
                   .admin(newUser.admin())
                   .build();

           userRepository.save(createdUser);

           return new UserResponse(
                   createdUser.getId(),
                   createdUser.getUsername(),
                   createdUser.getEmail(),
                   createdUser.getAdmin(),
                   createdUser.getActive()
           );
    }

    //Atualizar perfis
    public UserResponse updateUser(UserRequest updatedUser) {
        User user = userRepository.findById(updatedUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setUsername(updatedUser.username());
        user.setEmail(updatedUser.email());
        user.setPassword(passwordEncoder.encoder().encode(updatedUser.password()));
        user.setAdmin(updatedUser.admin());
        user.setActive(updatedUser.active());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAdmin(),
                user.getActive()
        );
    }
    //Desativar Perfis
}
