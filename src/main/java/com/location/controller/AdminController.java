package com.location.controller;

import com.location.model.*;
import com.location.repository.UtilisateurRepository;
import com.location.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VoitureService voitureService;
    private final ReservationService reservationService;
    private final MessageService messageService;
    private final UtilisateurRepository utilisateurRepository;
    private final AvisService avisService;                          // ← AJOUT

    public AdminController(VoitureService voitureService,
                           ReservationService reservationService,
                           MessageService messageService,
                           UtilisateurRepository utilisateurRepository,
                           AvisService avisService) {              // ← AJOUT
        this.voitureService = voitureService;
        this.reservationService = reservationService;
        this.messageService = messageService;
        this.utilisateurRepository = utilisateurRepository;
        this.avisService = avisService;                            // ← AJOUT
    }

    // ─── DASHBOARD ───────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Utilisateur admin = utilisateurRepository
                .findByUsername(auth.getName()).orElseThrow();

        model.addAttribute("totalVoitures",
                voitureService.getToutesVoitures().size());
        model.addAttribute("totalReservations",
                reservationService.getToutesReservations().size());
        model.addAttribute("messagesNonLus",
                messageService.countNonLus(admin));
        model.addAttribute("reservationsRecentes",
                reservationService.getToutesReservations()
                        .stream().limit(5).toList());

        // ← AJOUT : stats avis pour le dashboard
        model.addAttribute("statsAvis",
                avisService.getStatistiquesGlobales());
        model.addAttribute("avisEnAttente",
                avisService.getAvisEnAttente().size());

        return "admin/dashboard";
    }

    // ─── VOITURES ────────────────────────────────────────────
    @GetMapping("/voitures")
    public String listeVoitures(Model model) {
        model.addAttribute("voitures", voitureService.getToutesVoitures());
        return "admin/voitures";
    }

    @GetMapping("/voitures/nouveau")
    public String formulaireAjout(Model model) {
        model.addAttribute("voiture", new Voiture());
        return "admin/formulaire-voiture";
    }

    @GetMapping("/voitures/modifier/{id}")
    public String formulaireModif(@PathVariable Long id, Model model) {
        model.addAttribute("voiture", voitureService.findById(id));
        return "admin/formulaire-voiture";
    }

    @PostMapping("/voitures/sauvegarder")
    public String sauvegarder(@ModelAttribute Voiture voiture,
                              @RequestParam(value = "photo", required = false)
                              MultipartFile photo) throws IOException {
        voitureService.sauvegarderAvecPhoto(voiture, photo);
        return "redirect:/admin/voitures";
    }

    @GetMapping("/voitures/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        voitureService.supprimer(id);
        return "redirect:/admin/voitures";
    }

    // ─── RÉSERVATIONS ────────────────────────────────────────
    @GetMapping("/reservations")
    public String listeReservations(Model model) {
        model.addAttribute("reservations",
                reservationService.getToutesReservations());
        return "admin/reservations";
    }

    @PostMapping("/reservations/statut/{id}")
    public String changerStatut(@PathVariable Long id,
                                @RequestParam String statut) {
        reservationService.changerStatut(id, statut);
        return "redirect:/admin/reservations";
    }

    @GetMapping("/reservations/supprimer/{id}")
    public String supprimerReservation(@PathVariable Long id) {
        reservationService.supprimerReservation(id);
        return "redirect:/admin/reservations";
    }

    // ─── MESSAGERIE ──────────────────────────────────────────
    @GetMapping("/messages")
    public String listeClients(Model model) {
        List<Utilisateur> clients =
                utilisateurRepository.findByRole("ROLE_CLIENT");
        model.addAttribute("clients", clients);
        return "admin/messages-liste";
    }

    @GetMapping("/messages/{clientId}")
    public String conversation(@PathVariable Long clientId,
                               Model model, Authentication auth) {
        Utilisateur admin = utilisateurRepository
                .findByUsername(auth.getName()).orElseThrow();
        Utilisateur client = utilisateurRepository
                .findById(clientId).orElseThrow();
        messageService.marquerCommeLus(client, admin);
        model.addAttribute("messages",
                messageService.getConversation(admin, client));
        model.addAttribute("client", client);
        model.addAttribute("adminId", admin.getId());
        return "admin/conversation";
    }

    @PostMapping("/messages/{clientId}/envoyer")
    public String envoyerMessage(@PathVariable Long clientId,
                                 @RequestParam String contenu,
                                 Authentication auth) {
        Utilisateur admin = utilisateurRepository
                .findByUsername(auth.getName()).orElseThrow();
        Utilisateur client = utilisateurRepository
                .findById(clientId).orElseThrow();
        messageService.envoyerMessage(contenu, admin, client);
        return "redirect:/admin/messages/" + clientId;
    }

    // ─── AVIS ────────────────────────────────────────────────

    /**
     * Liste tous les avis avec filtrage par statut
     * GET /admin/avis
     * GET /admin/avis?statut=EN_ATTENTE
     */
    @GetMapping("/avis")
    public String listeAvis(
            @RequestParam(required = false, defaultValue = "TOUS") String statut,
            Model model) {

        List<Avis> avis = switch (statut) {
            case "EN_ATTENTE" -> avisService.getAvisEnAttente();
            case "APPROUVE"   -> avisService.getTousLesAvis().stream()
                    .filter(a -> "APPROUVE".equals(a.getStatut())).toList();
            case "REJETE"     -> avisService.getTousLesAvis().stream()
                    .filter(a -> "REJETE".equals(a.getStatut())).toList();
            default           -> avisService.getTousLesAvis();
        };

        model.addAttribute("avis", avis);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("statsAvis", avisService.getStatistiquesGlobales());
        return "admin/avis";
    }

    /**
     * Approuver un avis
     * POST /admin/avis/approuver/{id}
     */
    @PostMapping("/avis/approuver/{id}")
    public String approuverAvis(@PathVariable Long id,
                                @RequestParam(defaultValue = "TOUS") String retour) {
        avisService.approuver(id);
        return "redirect:/admin/avis?statut=" + retour;
    }

    /**
     * Rejeter un avis
     * POST /admin/avis/rejeter/{id}
     */
    @PostMapping("/avis/rejeter/{id}")
    public String rejeterAvis(@PathVariable Long id,
                              @RequestParam(defaultValue = "TOUS") String retour) {
        avisService.rejeter(id);
        return "redirect:/admin/avis?statut=" + retour;
    }

    /**
     * Supprimer un avis
     * GET /admin/avis/supprimer/{id}
     */
    @GetMapping("/avis/supprimer/{id}")
    public String supprimerAvis(@PathVariable Long id,
                                @RequestParam(defaultValue = "TOUS") String retour) {
        avisService.supprimer(id);
        return "redirect:/admin/avis?statut=" + retour;
    }
}