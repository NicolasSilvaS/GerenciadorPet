package com.gerenciadorPet.service;

import com.gerenciadorPet.dto.user.UpdateUserRequest;
import com.gerenciadorPet.dto.user.UserRequest;
import com.gerenciadorPet.model.User;
import com.gerenciadorPet.model.UserRole;
import com.gerenciadorPet.repository.UserRepository;
import com.gerenciadorPet.utils.BCryptEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        BCryptEncoder encoder = mock(BCryptEncoder.class);
        when(encoder.encoder()).thenReturn(passwordEncoder);
        adminService = new AdminService(userRepository, encoder);
    }

    @Test
    void shouldCreateActiveAdminUser() {
        UserRequest request = new UserRequest(null, "joao", "joao@email.com", "senha123", UserRole.ADMIN, true);
        when(userRepository.existsByUsername("joao")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10);
            return user;
        });

        var response = adminService.createUser(request);

        assertEquals(10, response.id());
        assertEquals("joao", response.username());
        assertEquals(UserRole.ADMIN, response.admin());
        assertTrue(response.active());
    }

    @Test
    void shouldRejectDuplicatedUsername() {
        UserRequest request = new UserRequest(null, "joao", "joao@email.com", "senha123", UserRole.STANDARD, true);
        when(userRepository.existsByUsername("joao")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> adminService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateRoleAndStatus() {
        User user = User.builder().id(5).username("antes").email("teste@email.com").password("hash").active(true).admin(UserRole.STANDARD).build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndIdNot("novo", 5L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = adminService.updateUser(new UpdateUserRequest(5L, "novo", true, false));

        assertEquals("novo", response.username());
        assertEquals(UserRole.ADMIN, response.admin());
        assertFalse(response.active());
    }

    @Test
    void shouldDeactivateUser() {
        User user = User.builder().id(7).username("maria").email("maria@email.com").password("hash").active(true).admin(UserRole.STANDARD).build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = adminService.deactivateUser(7L);

        assertFalse(response.active());
        assertFalse(user.getActive());
    }
}
