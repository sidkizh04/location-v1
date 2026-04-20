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
    CommandLineRunner initDatabase(UtilisateurRepository repository, PasswordEncoder encoder) {
        return args -> {
            // On supprime l'ancien pour être sûr de repartir à zéro
            repository.findByUsername("admin").ifPresent(repository::delete);

            // On crée le nouvel admin avec le bon encodage
            Utilisateur admin = new Utilisateur();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123")); // C'est Spring qui crypte ici
            admin.setRole("ROLE_ADMIN");

            repository.save(admin);
            System.out.println("-----------------------------------------");
            System.out.println("UTILISATEUR ADMIN CRÉÉ : admin / admin123");
            System.out.println("-----------------------------------------");
        };
    }
}