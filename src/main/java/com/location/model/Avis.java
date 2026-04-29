package com.location.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "avis")
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer note; // 1 à 5

    @Column(length = 1000)
    private String commentaire;

    @Column(name = "date_avis")
    private LocalDate dateAvis;

    // EN_ATTENTE, APPROUVE, REJETE
    @Column(nullable = false)
    private String statut = "EN_ATTENTE";

    @ManyToOne
    @JoinColumn(name = "voiture_id", nullable = false)
    private Voiture voiture;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @PrePersist
    public void prePersist() {
        this.dateAvis = LocalDate.now();
    }

    // Getters
    public Long getId() { return id; }
    public Integer getNote() { return note; }
    public String getCommentaire() { return commentaire; }
    public LocalDate getDateAvis() { return dateAvis; }
    public String getStatut() { return statut; }
    public Voiture getVoiture() { return voiture; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public Reservation getReservation() { return reservation; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNote(Integer note) { this.note = note; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public void setDateAvis(LocalDate dateAvis) { this.dateAvis = dateAvis; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}