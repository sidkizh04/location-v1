package com.location.controller;

import com.location.model.Voiture;
import com.location.repository.VoitureRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin") // Toutes les URLs commenceront par /admin
public class AdminController {

    private final VoitureRepository voitureRepository;

    public AdminController(VoitureRepository voitureRepository) {
        this.voitureRepository = voitureRepository;
    }

    // 1. Afficher la liste des voitures pour l'admin
    @GetMapping("/voitures")
    public String listeVoitures(Model model) {
        model.addAttribute("voitures", voitureRepository.findAll());
        return "admin/voitures"; // Dossier templates/admin/voitures.html
    }

    // 2. Afficher le formulaire d'ajout
    @GetMapping("/voitures/nouveau")
    public String formulaireAjout(Model model) {
        model.addAttribute("voiture", new Voiture());
        return "admin/formulaire-voiture";
    }

    // 3. Enregistrer la voiture dans la base
    @PostMapping("/voitures/sauvegarder")
    public String sauvegarderVoiture(@ModelAttribute("voiture") Voiture voiture) {
        voitureRepository.save(voiture);
        return "redirect:/admin/voitures"; // Redirige vers la liste après ajout
    }

    // 4. Supprimer une voiture
    @GetMapping("/voitures/supprimer/{id}")
    public String supprimerVoiture(@PathVariable Long id) {
        voitureRepository.deleteById(id);
        return "redirect:/admin/voitures";
    }
}