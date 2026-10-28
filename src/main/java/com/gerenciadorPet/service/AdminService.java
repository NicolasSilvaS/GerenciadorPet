package com.gerenciadorPet.service;


import com.gerenciadorPet.dto.user.*;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    /*
    ============================================
                 FUNÇÕES DO ADMIN
    ============================================
    Não tem permissão para mexer nos pets só nos usuarios,
    se deletar vai ter q rolar cascade btw
     */

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

    //Listar Usuarios
    //Editar Perfis
    //Atualizar perfis
    //Desativar Perfis


    /*
    ============================================
              CONTROLE DE SENHAS
    ============================================
    Admin pode ter a possibilidade de trocar senha de usuarios tb
     */
}
