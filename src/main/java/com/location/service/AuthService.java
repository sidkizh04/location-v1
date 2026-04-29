package com.location.service;

import com.location.model.PasswordResetToken;
import com.location.model.Utilisateur;
import com.location.repository.PasswordResetTokenRepository;
import com.location.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       PasswordResetTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public boolean emailExiste(String email) {
        return utilisateurRepository.findByEmail(email).isPresent();
    }

    public boolean usernameExiste(String username) {
        return utilisateurRepository.findByUsername(username).isPresent();
    }

    public Utilisateur inscrireClient(String nom, String prenom,
                                      String email, String password) {
        String username = email.split("@")[0];
        if (usernameExiste(username)) {
            username = username + (int)(Math.random() * 1000);
        }

        // Générer token de vérification
        String token = UUID.randomUUID().toString();

        Utilisateur u = new Utilisateur();
        u.setUsername(username);
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole("ROLE_CLIENT");
        u.setEmailVerifie(false); // ← compte inactif par défaut
        u.setTokenVerification(token);

        utilisateurRepository.save(u);

        // Envoyer email de vérification
        emailService.envoyerVerificationEmail(email,
                prenom + " " + nom, token);

        return u;
    }

    public boolean verifierEmail(String token) {
        Optional<Utilisateur> opt = utilisateurRepository
                .findByTokenVerification(token);
        if (opt.isPresent()) {
            Utilisateur u = opt.get();
            u.setEmailVerifie(true);
            u.setTokenVerification(null); // supprimer le token
            utilisateurRepository.save(u);
            return true;
        }
        return false;
    }

    public void demanderReinitialisationMdp(String email) {
        Optional<Utilisateur> opt = utilisateurRepository.findByEmail(email);
        if (opt.isPresent()) {
            Utilisateur u = opt.get();
            tokenRepository.deleteByUtilisateur(u);
            String token = UUID.randomUUID().toString();
            PasswordResetToken prt = new PasswordResetToken();
            prt.setToken(token);
            prt.setUtilisateur(u);
            prt.setExpiration(LocalDateTime.now().plusHours(1));
            tokenRepository.save(prt);
            emailService.envoyerReinitialisationMdp(email, token);
        }
    }

    public boolean tokenValide(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> t.getExpiration().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public boolean reinitialiserMdp(String token, String nouveauMdp) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);
        if (opt.isPresent() &&
                opt.get().getExpiration().isAfter(LocalDateTime.now())) {
            Utilisateur u = opt.get().getUtilisateur();
            u.setPassword(passwordEncoder.encode(nouveauMdp));
            utilisateurRepository.save(u);
            tokenRepository.delete(opt.get());
            return true;
        }
        return false;
    }
}