package com.location.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "utilisateurs") // Correction : pluriel pour correspondre au SQL
@Data
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;
}