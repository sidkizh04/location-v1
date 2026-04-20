package com.location.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marque;
    private String modele;
    private Double prixParJour;
    private String imageUrL; // Pour afficher la photo de la voiture
    private boolean disponible = true;
}