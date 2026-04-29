package com.location.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerReinitialisationMdp(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe - RIDE WITH LAAMARI");
        message.setText(
                "Bonjour,\n\n" +
                        "Vous avez demandé une réinitialisation de votre mot de passe.\n\n" +
                        "Cliquez sur ce lien pour continuer :\n" +
                        "http://localhost:8080/reset-password?token=" + token + "\n\n" +
                        "Ce lien expire dans 1 heure.\n\n" +
                        "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
                        "L'équipe RIDE WITH LAAMARI"
        );
        mailSender.send(message);
    }

    public void envoyerConfirmationReservation(String email, String nomClient,
                                               String voiture, String dateDebut,
                                               String dateFin, Double prix) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmation de réservation - RIDE WITH LAAMARI");
        message.setText(
                "Bonjour " + nomClient + ",\n\n" +
                        "Votre réservation a été enregistrée avec succès !\n\n" +
                        "Détails :\n" +
                        "Véhicule : " + voiture + "\n" +
                        "Du : " + dateDebut + "\n" +
                        "Au : " + dateFin + "\n" +
                        "Prix total : " + prix + " €\n\n" +
                        "Notre équipe vous contactera pour confirmer votre réservation.\n\n" +
                        "Merci de votre confiance,\n" +
                        "L'équipe RIDE WITH LAAMARI"
        );
        mailSender.send(message);
    }

    // ← NOUVEAU : email de vérification
    public void envoyerVerificationEmail(String email, String nom,
                                         String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmez votre email - RIDE WITH LAAMARI");
        message.setText(
                "Bonjour " + nom + ",\n\n" +
                        "Merci de vous être inscrit sur RIDE WITH LAAMARI !\n\n" +
                        "Pour activer votre compte, cliquez sur ce lien :\n" +
                        "http://localhost:8080/verifier-email?token=" + token + "\n\n" +
                        "Ce lien est valable 24 heures.\n\n" +
                        "Si vous n'avez pas créé de compte, ignorez cet email.\n\n" +
                        "L'équipe RIDE WITH LAAMARI"
        );
        mailSender.send(message);
    }
}