package com.location.service;

import com.location.model.Reservation;
import com.location.model.Utilisateur;
import com.location.model.Voiture;
import com.location.repository.ReservationRepository;
import com.location.repository.VoitureRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VoitureRepository voitureRepository;
    private final EmailService emailService;

    private final String uploadDir = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "static"
            + File.separator + "uploads" + File.separator + "documents"
            + File.separator;

    public ReservationService(ReservationRepository reservationRepository,
                              VoitureRepository voitureRepository,
                              EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.voitureRepository = voitureRepository;
        this.emailService = emailService;
    }

    private String sauvegarderFichier(MultipartFile fichier,
                                      String sousDossier) throws IOException {
        if (fichier == null || fichier.isEmpty()) return null;
        String dossier = uploadDir + sousDossier + File.separator;
        String nomFichier = UUID.randomUUID() + "_"
                + fichier.getOriginalFilename()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        Path chemin = Paths.get(dossier + nomFichier);
        Files.createDirectories(chemin.getParent());
        Files.copy(fichier.getInputStream(), chemin,
                StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/documents/" + sousDossier + "/" + nomFichier;
    }

    public Reservation creerReservation(Reservation reservation,
                                        MultipartFile permisRecto,
                                        MultipartFile permisVerso,
                                        MultipartFile documentRecto,
                                        MultipartFile documentVerso)
            throws IOException {

        long jours = ChronoUnit.DAYS.between(
                reservation.getDateDebut(), reservation.getDateFin());
        if (jours <= 0) jours = 1;
        reservation.setPrixTotal(
                jours * reservation.getVoiture().getPrixParJour());
        reservation.setStatut("EN_ATTENTE");

        // Sauvegarder les images
        reservation.setPermisRecto(sauvegarderFichier(permisRecto, "permis"));
        reservation.setPermisVerso(sauvegarderFichier(permisVerso, "permis"));
        reservation.setDocumentRecto(
                sauvegarderFichier(documentRecto, reservation.getTypeDocument()
                        .toLowerCase()));
        reservation.setDocumentVerso(
                sauvegarderFichier(documentVerso, reservation.getTypeDocument()
                        .toLowerCase()));

        // Marquer voiture indisponible
        Voiture v = reservation.getVoiture();
        v.setDisponible(false);
        voitureRepository.save(v);

        Reservation saved = reservationRepository.save(reservation);

        // Email de confirmation
        if (reservation.getUtilisateur().getEmail() != null) {
            try {
                emailService.envoyerConfirmationReservation(
                        reservation.getUtilisateur().getEmail(),
                        reservation.getPrenom() + " " + reservation.getNom(),
                        v.getMarque() + " " + v.getModele(),
                        reservation.getDateDebut().toString(),
                        reservation.getDateFin().toString(),
                        saved.getPrixTotal()
                );
            } catch (Exception e) {
                System.out.println("Email non envoye : " + e.getMessage());
            }
        }
        return saved;
    }

    public List<Reservation> getToutesReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsClient(Utilisateur u) {
        return reservationRepository.findByUtilisateur(u);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public void changerStatut(Long id, String statut) {
        reservationRepository.findById(id).ifPresent(r -> {
            r.setStatut(statut);
            if ("ANNULEE".equals(statut)) {
                r.getVoiture().setDisponible(true);
                voitureRepository.save(r.getVoiture());
            }
            reservationRepository.save(r);
        });
    }

    public void supprimerReservation(Long id) {
        reservationRepository.findById(id).ifPresent(r -> {
            r.getVoiture().setDisponible(true);
            voitureRepository.save(r.getVoiture());
            reservationRepository.deleteById(id);
        });
    }
}