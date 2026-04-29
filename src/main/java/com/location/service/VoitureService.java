package com.location.service;

import com.location.model.Voiture;
import com.location.repository.VoitureRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.io.File;

@Service
public class VoitureService {

    private final VoitureRepository voitureRepository;

    public VoitureService(VoitureRepository voitureRepository) {
        this.voitureRepository = voitureRepository;
    }

    public List<Voiture> getToutesVoitures() {
        return voitureRepository.findAll();
    }

    public List<Voiture> getVoituresDisponibles() {
        return voitureRepository.findByDisponibleTrue();
    }

    public Voiture sauvegarderAvecPhoto(Voiture voiture, MultipartFile photo)
            throws IOException {
        if (photo != null && !photo.isEmpty()) {
            // Dossier ABSOLU dans target (servi directement par Spring Boot)
            String uploadDir = System.getProperty("user.dir")
                    + File.separator + "src" + File.separator + "main"
                    + File.separator + "resources" + File.separator
                    + "static" + File.separator + "images"
                    + File.separator + "voitures" + File.separator;

            String nomFichier = UUID.randomUUID() + "_"
                    + photo.getOriginalFilename()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            Path chemin = Paths.get(uploadDir + nomFichier);
            Files.createDirectories(chemin.getParent());
            Files.copy(photo.getInputStream(), chemin,
                    StandardCopyOption.REPLACE_EXISTING);

            voiture.setImageUrL("/images/voitures/" + nomFichier);
        }
        return voitureRepository.save(voiture);
    }

    public void supprimer(Long id) {
        voitureRepository.deleteById(id);
    }

    public Voiture findById(Long id) {
        return voitureRepository.findById(id).orElseThrow();
    }
}