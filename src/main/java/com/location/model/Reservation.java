package com.location.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double prixTotal;
    private String statut = "EN_ATTENTE";

    // Infos personnelles
    private String nom;
    private String prenom;
    private String telephone;

    // Type de document : "CIN" ou "PASSPORT"
    private String typeDocument;
    private String numeroDocument;

    // Chemins des images uploadées
    private String permisRecto;
    private String permisVerso;
    private String documentRecto;
    private String documentVerso;

    @ManyToOne
    @JoinColumn(name = "voiture_id")
    private Voiture voiture;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // Getters
    public Long getId() { return id; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public Double getPrixTotal() { return prixTotal; }
    public String getStatut() { return statut; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getTypeDocument() { return typeDocument; }
    public String getNumeroDocument() { return numeroDocument; }
    public String getPermisRecto() { return permisRecto; }
    public String getPermisVerso() { return permisVerso; }
    public String getDocumentRecto() { return documentRecto; }
    public String getDocumentVerso() { return documentVerso; }
    public Voiture getVoiture() { return voiture; }
    public Utilisateur getUtilisateur() { return utilisateur; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setTypeDocument(String typeDocument) { this.typeDocument = typeDocument; }
    public void setNumeroDocument(String numeroDocument) { this.numeroDocument = numeroDocument; }
    public void setPermisRecto(String permisRecto) { this.permisRecto = permisRecto; }
    public void setPermisVerso(String permisVerso) { this.permisVerso = permisVerso; }
    public void setDocumentRecto(String documentRecto) { this.documentRecto = documentRecto; }
    public void setDocumentVerso(String documentVerso) { this.documentVerso = documentVerso; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}