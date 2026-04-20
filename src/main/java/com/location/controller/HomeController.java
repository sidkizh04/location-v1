package com.location.controller;

import com.location.model.Voiture;
import com.location.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    // On injecte le repository pour accéder aux voitures en base de données
    @Autowired
    private VoitureRepository voitureRepository;

    @GetMapping("/")
    public String accueil(Model model) {
        // 1. On récupère toutes les voitures de la base de données
        List<Voiture> listeVoitures = voitureRepository.findAll();

        // 2. On ajoute les données au modèle pour Thymeleaf
        model.addAttribute("nomSite", "RIDE WITH LAAMARI");
        model.addAttribute("voitures", listeVoitures);

        // 3. On retourne la page index.html
        return "index";
    }
}