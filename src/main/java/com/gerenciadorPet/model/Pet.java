package com.gerenciadorPet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "pets")
@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private double height;

    @Column(length = 100, nullable = false)
    private double weight;

    @Column(length = 100, nullable = false)
    private String breed;

    @Column(length = 100, nullable = false)
    private LocalDateTime bornIn;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "pet")
    List<Historic> petLog;

    @OneToMany(mappedBy = "pet")
    private List<Appointment> appointments = new ArrayList<>();

    @Column(length = 500)
    private String imageUrl;
}
