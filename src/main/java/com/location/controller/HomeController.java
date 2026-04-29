package com.location.controller;

import com.location.model.Avis;
import com.location.model.Voiture;
import com.location.service.AvisService;
import com.location.service.VoitureService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    private final VoitureService voitureService;
    private final AvisService avisService;

    public HomeController(VoitureService voitureService,
                          AvisService avisService) {
        this.voitureService = voitureService;
        this.avisService = avisService;
    }

    // ─── PAGE ACCUEIL ─────────────────────────────────────────
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("voitures",
                voitureService.getVoituresDisponibles());

        // 3 derniers avis approuvés pour la section témoignages
        List<Avis> derniersAvis = avisService.getTousLesAvis()
                .stream()
                .filter(a -> "APPROUVE".equals(a.getStatut()))
                .sorted((a, b) -> b.getDateAvis().compareTo(a.getDateAvis()))
                .limit(3)
                .toList();
        model.addAttribute("derniersAvis", derniersAvis);

        return "index";
    }

    // ─── CATALOGUE ────────────────────────────────────────────
    @GetMapping("/voitures")
    public String catalogue(Model model) {
        model.addAttribute("voitures",
                voitureService.getVoituresDisponibles());
        return "voitures";
    }

    // ─── DÉTAIL VOITURE ───────────────────────────────────────
    @GetMapping("/voitures/{id}")
    public String detailVoiture(@PathVariable Long id, Model model) {
        Voiture voiture = voitureService.findById(id);

        model.addAttribute("voiture", voiture);

        // Avis approuvés de cette voiture
        model.addAttribute("avisApprouves",
                avisService.getAvisApprouves(voiture));

        // Moyenne des notes
        model.addAttribute("moyenneNote",
                avisService.getMoyenneNote(voiture));

        // Nombre d'avis approuvés
        model.addAttribute("nombreAvis",
                avisService.getNombreAvis(voiture));

        // Autres voitures disponibles (hors voiture actuelle, max 3)
        model.addAttribute("autresVoitures",
                voitureService.getVoituresDisponibles()
                        .stream()
                        .filter(v -> !v.getId().equals(id))
                        .limit(3)
                        .toList());

        return "voiture-detail";
    }

    // ─── REDIRECTION APRÈS LOGIN ──────────────────────────────
    @GetMapping("/redirect-role")
    public String redirectParRole(Authentication auth) {
        if (auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/client/dashboard";
    }
}