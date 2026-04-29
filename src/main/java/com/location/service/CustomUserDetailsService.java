package com.location.service;

import com.location.model.Utilisateur;
import com.location.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOuUsername)
            throws UsernameNotFoundException {

        Utilisateur user = utilisateurRepository
                .findByEmail(emailOuUsername)
                .orElseGet(() -> utilisateurRepository
                        .findByUsername(emailOuUsername)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "Utilisateur non trouve : " + emailOuUsername)));

        // Bloquer si email non vérifié (sauf pour l'admin)
        if (!user.isEmailVerifie() && !"ROLE_ADMIN".equals(user.getRole())) {
            throw new UsernameNotFoundException(
                    "EMAIL_NON_VERIFIE");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}