package com.gerenciadorPet.service;


import com.gerenciadorPet.dto.auth.*;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    /*
    ============================================
                 FUNÇÕES DO USUARIO
    ============================================
     */
    //Login do Usuario
    public UserLoginResponse loginUser(UserLoginRequest userLogin) {
        User newUser = userRepository.findbyUsername(userLogin.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Username not found"));

        if(!userLogin.password().equals(newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha invalida");
        }

        return new UserLoginResponse(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getEmail()
                );
    }



    //Registrar Usuario
    public UserRegisterResponse registerUser(UserRegisterRequest userRegister) {

        User newUser = User.builder()
                .username(userRegister.username())
                .email(userRegister.email())
                .password(userRegister.password())// vou criar metodo depois para dar hash nisso
                .admin(UserRole.STANDARD)
                .active(true)
                .build();

        if(userRepository.existsByUsername(userRegister.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Username já está em uso");
        }

        userRepository.save(newUser);

        return new UserRegisterResponse(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUsername()
                );
    }
    /*
    ============================================
            CONTROLE DE SENHAS E RECUPERACAO
    ============================================
     */
    //Pedir Recuperação de Senha -> Manda email e vai para tela de trocar senha, troca a senha mas não precisa colocar senha
    /*Editar SenhaRecuperacao -> proximo passo de Recuperacao de senha,

    só temporario prefiro procurar algo q não esteja diretamente no usuario service
    */

    //Editar Senha -> Não manda email de verificacao mas pede senha
    //Editar Nome
    //Editar Email -> Manda email de verificação de autoria
}
