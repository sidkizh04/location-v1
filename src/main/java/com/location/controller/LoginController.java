package com.location.controller;

import com.location.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            // Message spécial si email non vérifié
            model.addAttribute("error",
                    "Email ou mot de passe incorrect. " +
                            "Si vous venez de vous inscrire, verifiez votre email.");
        }
        if (logout != null)
            model.addAttribute("message", "Deconnexion reussie.");
        return "login";
    }

    @GetMapping("/inscription")
    public String inscription() {
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(@RequestParam String nom,
                           @RequestParam String prenom,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error",
                    "Les mots de passe ne correspondent pas.");
            return "inscription";
        }
        if (authService.emailExiste(email)) {
            model.addAttribute("error", "Cet email est deja utilise.");
            return "inscription";
        }
        authService.inscrireClient(nom, prenom, email, password);
        return "redirect:/inscription?succes";
    }

    // ← NOUVEAU : vérification email
    @GetMapping("/verifier-email")
    public String verifierEmail(@RequestParam String token, Model model) {
        if (authService.verifierEmail(token)) {
            return "redirect:/login?email-verifie";
        }
        model.addAttribute("error",
                "Lien invalide ou expiré. Veuillez vous réinscrire.");
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email,
                                     Model model) {
        authService.demanderReinitialisationMdp(email);
        model.addAttribute("message",
                "Si cet email existe, un lien a ete envoye.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {
        if (!authService.tokenValide(token)) {
            model.addAttribute("error", "Ce lien est invalide ou expire.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam String token,
                                    @RequestParam String password,
                                    Model model) {
        if (authService.reinitialiserMdp(token, password)) {
            return "redirect:/login?reset";
        }
        model.addAttribute("error", "Lien invalide ou expire.");
        return "reset-password";
    }
}