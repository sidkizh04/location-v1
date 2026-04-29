package com.location.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu = false;

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;

    // Getters
    public Long getId() { return id; }
    public String getContenu() { return contenu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public boolean isLu() { return lu; }
    public Utilisateur getExpediteur() { return expediteur; }
    public Utilisateur getDestinataire() { return destinataire; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public void setLu(boolean lu) { this.lu = lu; }
    public void setExpediteur(Utilisateur expediteur) { this.expediteur = expediteur; }
    public void setDestinataire(Utilisateur destinataire) { this.destinataire = destinataire; }
}