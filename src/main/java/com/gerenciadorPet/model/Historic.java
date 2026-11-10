package com.gerenciadorPet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "pet_log")
@Entity
public class Historic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 300, nullable = false)
    private String description;

    @Column(length = 100, nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoricType type;

    @ManyToOne
    @JoinColumn(name = "pet")
    private Pet pet;
}
