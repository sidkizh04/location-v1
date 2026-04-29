package com.location;

import com.location.model.Utilisateur;
import com.location.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UtilisateurRepository repository,
            PasswordEncoder encoder) {
        return args -> {
            // Créer admin seulement s'il n'existe pas déjà
            if (repository.findByUsername("admin").isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setEmail("admin@laamari.com");
                admin.setNom("Administrateur");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                repository.save(admin);
                System.out.println("ADMIN CREE : admin / admin123");
            } else {
                System.out.println("ADMIN EXISTE DEJA");
            }
        };
    }
}