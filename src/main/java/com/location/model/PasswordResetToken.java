package com.location.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    public Long getId() { return id; }
    public String getToken() { return token; }
    public LocalDateTime getExpiration() { return expiration; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setId(Long id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setExpiration(LocalDateTime expiration) { this.expiration = expiration; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}