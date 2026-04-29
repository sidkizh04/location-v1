package com.location.controller;

import com.location.model.*;
import com.location.repository.UtilisateurRepository;
import com.location.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ReservationService reservationService;
    private final VoitureService voitureService;
    private final MessageService messageService;
    private final UtilisateurRepository utilisateurRepository;
    private final AvisService avisService;                          // ← AJOUT

    public ClientController(ReservationService reservationService,
                            VoitureService voitureService,
                            MessageService messageService,
                            UtilisateurRepository utilisateurRepository,
                            AvisService avisService) {             // ← AJOUT
        this.reservationService = reservationService;
        this.voitureService = voitureService;
        this.messageService = messageService;
        this.utilisateurRepository = utilisateurRepository;
        this.avisService = avisService;                            // ← AJOUT
    }

    private Utilisateur getUtilisateur(Authentication auth) {
        return utilisateurRepository.findByUsername(auth.getName()).orElseThrow();
    }

    // ─── DASHBOARD ───────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Utilisateur client = getUtilisateur(auth);
        List<Reservation> reservations =
                reservationService.getReservationsClient(client);

        long confirmees = reservations.stream()
                .filter(r -> "CONFIRMEE".equals(r.getStatut()))
                .count();

        // ← AJOUT : pour chaque réservation, on sait si un avis existe déjà
        // On construit une Map<reservationId, Boolean> passée au template
        java.util.Map<Long, Boolean> avisExistants = new java.util.HashMap<>();
        for (Reservation r : reservations) {
            boolean eligible = "CONFIRMEE".equals(r.getStatut())
                    || "TERMINEE".equals(r.getStatut());
            if (eligible) {
                avisExistants.put(r.getId(), avisService.aDejaUnAvis(r.getId()));
            }
        }

        model.addAttribute("client", client);
        model.addAttribute("reservations", reservations);
        model.addAttribute("messagesNonLus", messageService.countNonLus(client));
        model.addAttribute("reservationConfirmees", confirmees);
        model.addAttribute("avisExistants", avisExistants);        // ← AJOUT
        return "client/dashboard";
    }

    // ─── FORMULAIRE RÉSERVATION ──────────────────────────────
    @GetMapping("/reserver/{voitureId}")
    public String formulaireReservation(@PathVariable Long voitureId,
                                        Model model, Authentication auth) {
        Utilisateur client = getUtilisateur(auth);
        model.addAttribute("voiture", voitureService.findById(voitureId));
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("client", client);
        return "client/reservation-form";
    }

    // ─── CONFIRMER RÉSERVATION ───────────────────────────────
    @PostMapping("/reserver/{voitureId}")
    public String confirmerReservation(
            @PathVariable Long voitureId,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("telephone") String telephone,
            @RequestParam("typeDocument") String typeDocument,
            @RequestParam("numeroDocument") String numeroDocument,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam("permisRecto") MultipartFile permisRecto,
            @RequestParam("permisVerso") MultipartFile permisVerso,
            @RequestParam("documentRecto") MultipartFile documentRecto,
            @RequestParam("documentVerso") MultipartFile documentVerso,
            Authentication auth) throws Exception {

        Utilisateur client = getUtilisateur(auth);
        Voiture voiture = voitureService.findById(voitureId);

        Reservation reservation = new Reservation();
        reservation.setNom(nom);
        reservation.setPrenom(prenom);
        reservation.setTelephone(telephone);
        reservation.setTypeDocument(typeDocument);
        reservation.setNumeroDocument(numeroDocument);
        reservation.setDateDebut(LocalDate.parse(dateDebut));
        reservation.setDateFin(LocalDate.parse(dateFin));
        reservation.setVoiture(voiture);
        reservation.setUtilisateur(client);

        reservationService.creerReservation(reservation,
                permisRecto, permisVerso, documentRecto, documentVerso);

        return "redirect:/client/dashboard?reserve";
    }

    // ─── ANNULER RÉSERVATION ─────────────────────────────────
    @GetMapping("/reservations/annuler/{id}")
    public String annulerReservation(@PathVariable Long id,
                                     Authentication auth) {
        Utilisateur client = getUtilisateur(auth);
        reservationService.findById(id).ifPresent(r -> {
            if (r.getUtilisateur().getId().equals(client.getId())) {
                reservationService.changerStatut(id, "ANNULEE");
            }
        });
        return "redirect:/client/dashboard";
    }

    // ─── MESSAGERIE ──────────────────────────────────────────
    @GetMapping("/messages")
    public String conversation(Model model, Authentication auth) {
        Utilisateur client = getUtilisateur(auth);
        Utilisateur admin = utilisateurRepository.findByRole("ROLE_ADMIN")
                .stream().findFirst().orElseThrow();
        messageService.marquerCommeLus(admin, client);
        model.addAttribute("messages",
                messageService.getConversation(client, admin));
        model.addAttribute("adminId", admin.getId());
        return "client/messages";
    }

    @PostMapping("/messages/envoyer")
    public String envoyerMessage(@RequestParam String contenu,
                                 Authentication auth) {
        Utilisateur client = getUtilisateur(auth);
        Utilisateur admin = utilisateurRepository.findByRole("ROLE_ADMIN")
                .stream().findFirst().orElseThrow();
        messageService.envoyerMessage(contenu, client, admin);
        return "redirect:/client/messages";
    }

    // ─── AVIS ────────────────────────────────────────────────

    /**
     * Affiche le formulaire pour laisser un avis
     * Accessible via : GET /client/avis/formulaire/{reservationId}
     */
    @GetMapping("/avis/formulaire/{reservationId}")
    public String formulaireAvis(@PathVariable Long reservationId,
                                 Model model, Authentication auth) {
        Utilisateur client = getUtilisateur(auth);

        // Vérifie que la réservation appartient au client
        return reservationService.findById(reservationId)
                .filter(r -> r.getUtilisateur().getId().equals(client.getId()))
                .filter(r -> "CONFIRMEE".equals(r.getStatut())
                        || "TERMINEE".equals(r.getStatut()))
                .map(r -> {
                    // Redirige si avis déjà soumis
                    if (avisService.aDejaUnAvis(reservationId)) {
                        return "redirect:/client/dashboard?avis-existe";
                    }
                    model.addAttribute("reservation", r);
                    model.addAttribute("voiture", r.getVoiture());
                    return "client/avis-form";
                })
                .orElse("redirect:/client/dashboard?non-autorise");
    }

    /**
     * Soumettre un avis
     * Accessible via : POST /client/avis/soumettre
     */
    @PostMapping("/avis/soumettre")
    public String soumettreAvis(@RequestParam Long reservationId,
                                @RequestParam Integer note,
                                @RequestParam(required = false) String commentaire,
                                Authentication auth) {
        Utilisateur client = getUtilisateur(auth);

        boolean succes = avisService.laisserAvis(
                reservationId, note, commentaire, client);

        if (succes) {
            return "redirect:/client/dashboard?avis-envoye";
        } else {
            return "redirect:/client/dashboard?avis-erreur";
        }
    }
}