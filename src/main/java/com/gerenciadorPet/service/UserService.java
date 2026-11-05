package com.gerenciadorPet.service;


import com.gerenciadorPet.dto.auth.*;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.dto.user.UserResponse;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import com.gerenciadorPet.utils.BCryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptEncoder passwordEncoder;
    /*
    ============================================
                 FUNÇÕES DO USUARIO
    ============================================
     */
    //Login do Usuario
    public UserLoginResponse loginUser(UserLoginRequest userLogin) {
        User newUser = userRepository.findbyUsername(userLogin.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));

        if(!passwordEncoder.encoder().matches(userLogin.password(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha invalida");
        }

        return new UserLoginResponse(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getAdmin(),
                newUser.getActive()
                );
    }



    //Registrar Usuario
    public UserRegisterResponse registerUser(UserRegisterRequest userRegister) {

        User newUser = User.builder()
                .username(userRegister.username())
                .email(userRegister.email())
                .password(passwordEncoder.encoder().encode(userRegister.password()))
                .admin(UserRole.STANDARD)
                .active(true)
                .build();

        if(userRepository.existsByUsername(userRegister.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username já está em uso");
        }

        userRepository.save(newUser);

        return new UserRegisterResponse(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUsername(),
                newUser.getAdmin(),
                newUser.getActive()
                );
    }
    /*
    ============================================
            CONTROLE DE SENHAS E RECUPERACAO
    ============================================
     */

    public UserResponse updateUser(UserRequest updatedUser) {
        User user = userRepository.findById(updatedUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autorizado"));

        //Editar Senha
        if(passwordEncoder.encoder().matches(updatedUser.password(), user.getPassword()) && user.getPassword() != null) {
            user.setPassword(updatedUser.password());
        }

        //Editar nome
        if((updatedUser.username()) != null) {
            user.setPassword(passwordEncoder.encoder().encode(updatedUser.password()));
        }

        //Editar email
        if((updatedUser.email()) != null) {
            user.setEmail(updatedUser.email());
        }

        user = User.builder()
                .username(updatedUser.username())
                .email(updatedUser.email())
                .password(passwordEncoder.encoder().encode(updatedUser.password()))
                .admin(UserRole.STANDARD)
                .active(true)
                .build();

        userRepository.save(user);

        // Inclui o perfil e o estado atual do usuário na resposta.
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAdmin(),
                user.getActive()
                );
    }

    public void recuperarSenha() {

    }
}
