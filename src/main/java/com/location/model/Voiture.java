package com.location.model;

import jakarta.persistence.*;

@Entity
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marque;
    private String modele;
    private Double prixParJour;
    private String imageUrL;
    private boolean disponible = true;
    private String carburant = "Essence";

    // Nouveaux champs pour la page détail
    private String transmission = "Automatique"; // Automatique, Manuelle
    private Integer nombrePlaces = 5;
    private Integer nombrePortes = 4;
    private String couleur;
    private Integer annee;
    private String description;
    private Integer kilometrage;
    private String climatisation = "Oui";

    // Getters existants
    public Long getId() { return id; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public Double getPrixParJour() { return prixParJour; }
    public String getImageUrL() { return imageUrL; }
    public boolean isDisponible() { return disponible; }
    public String getCarburant() { return carburant; }

    // Nouveaux getters
    public String getTransmission() { return transmission; }
    public Integer getNombrePlaces() { return nombrePlaces; }
    public Integer getNombrePortes() { return nombrePortes; }
    public String getCouleur() { return couleur; }
    public Integer getAnnee() { return annee; }
    public String getDescription() { return description; }
    public Integer getKilometrage() { return kilometrage; }
    public String getClimatisation() { return climatisation; }

    // Setters existants
    public void setId(Long id) { this.id = id; }
    public void setMarque(String marque) { this.marque = marque; }
    public void setModele(String modele) { this.modele = modele; }
    public void setPrixParJour(Double prixParJour) { this.prixParJour = prixParJour; }
    public void setImageUrL(String imageUrL) { this.imageUrL = imageUrL; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setCarburant(String carburant) { this.carburant = carburant; }

    // Nouveaux setters
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    public void setNombrePortes(Integer nombrePortes) { this.nombrePortes = nombrePortes; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public void setDescription(String description) { this.description = description; }
    public void setKilometrage(Integer kilometrage) { this.kilometrage = kilometrage; }
    public void setClimatisation(String climatisation) { this.climatisation = climatisation; }
}