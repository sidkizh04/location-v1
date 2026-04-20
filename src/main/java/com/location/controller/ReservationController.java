package com.location.controller;

import com.location.model.Reservation;
import com.location.model.Voiture;
import com.location.repository.ReservationRepository;
import com.location.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/reserver")
public class ReservationController {

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // Affiche le formulaire de réservation pour une voiture précise
    @GetMapping("/{id}")
    public String afficherFormulaire(@PathVariable Long id, Model model) {
        Voiture voiture = voitureRepository.findById(id).orElseThrow();
        model.addAttribute("voiture", voiture);
        model.addAttribute("reservation", new Reservation());
        return "reservation-form";
    }

    // Enregistre la réservation
    @PostMapping("/confirmer")
    public String confirmerReservation(@ModelAttribute Reservation reservation, @RequestParam Long voitureId) {
        Voiture voiture = voitureRepository.findById(voitureId).orElseThrow();

        // Calcul du prix total
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        reservation.setPrixTotal(jours * voiture.getPrixParJour());
        reservation.setVoiture(voiture);

        // On sauvegarde
        reservationRepository.save(reservation);

        // On peut aussi marquer la voiture comme non disponible
        voiture.setDisponible(false);
        voitureRepository.save(voiture);

        return "redirect:/?success";
    }
}