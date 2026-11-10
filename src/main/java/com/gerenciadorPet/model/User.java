package com.gerenciadorPet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(length = 100, nullable = false, unique = true)
        private String username;

        @Column(length = 100, nullable = false)
        private String email;

        @Column(length = 100, nullable = false)
        private String password;

        @Column(length = 36)
        private String passwordResetTokenId;

        @Column(nullable = false)
        private Boolean active;

        @Enumerated(EnumType.STRING)
        private UserRole admin;

        @OneToMany(mappedBy = "owner")
        List<Pet> pets;

}