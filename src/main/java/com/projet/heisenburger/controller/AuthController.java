package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Client;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.model.User;
import com.projet.heisenburger.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "home"; // Page d'accueil (inchangée pour l'instant)
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/client_inscription")
    public String registerForm() {
        return "/client/client_inscription";
    }

    @GetMapping("/restaurant_inscription")
    public String proRegisterForm() {
        return "/restaurant/restaurant_inscription";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (authService.authenticate(email, password)) {
            User user = authService.findUser(email);
            session.setAttribute("user", user); // Stocke l'objet User (Admin, Client, ou Restaurant)

            // Redirection en fonction du type d'utilisateur
            if (user instanceof Admin) {
                return "redirect:/admin/dashboard";
            } else if (user instanceof Client) {
                return "redirect:/client/dashboard";
            } else if (user instanceof Restaurant) {
                return "redirect:/restaurant/dashboard";
            }
            // Fallback si le type n'est pas géré (ne devrait pas arriver avec les types définis)
            model.addAttribute("error", "Type d'utilisateur inconnu !");
            return "login";
        } else {
            model.addAttribute("error", "Identifiants invalides !");
            return "login";
        }
    }

    // Tableau de bord Admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        if (!(userAttribute instanceof Admin)) { // Vérifie si l'utilisateur en session est un Admin
            return "redirect:/login"; // Redirige si non autorisé
        }
        model.addAttribute("user", userAttribute);
        return "admin/admin_dashboard";
    }
    @GetMapping("/admin_gestion_restaurant")
    public String adminGestionRestaurant(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        return "admin/admin_gestion_restaurant";
    }
    @GetMapping("/admin_gestion_client")
    public String adminGestionClient(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        return "admin/admin_gestion_client";
    }
    @GetMapping("/admin_gestion_commande")
    public String adminGestionCommande(HttpSession session, Model model){
        Object userAttribute = session.getAttribute("user");
        return "admin/admin_gestion_commande";
    }
    @GetMapping("/admin_profil")
    public String adminProfil(HttpSession session, Model model){
        Object userAttribute = session.getAttribute("user");
        return "admin/admin_profil";
    }

    // Tableau de bord Client
    @GetMapping("/client/dashboard")
    public String clientDashboard(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        if (!(userAttribute instanceof Client)) { // Vérifie si l'utilisateur en session est un Client
            return "redirect:/login";
        }
        model.addAttribute("user", userAttribute);
        return "client/client_dashboard";
    }

    // Tableau de bord Restaurant
    @GetMapping("/restaurant/dashboard")
    public String restaurantDashboard(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        if (!(userAttribute instanceof Restaurant)) { // Vérifie si l'utilisateur en session est un Restaurant
            return "redirect:/login";
        }
        model.addAttribute("user", userAttribute);
        return "restaurant/restaurant_dashboard";
    }

    // L'ancien /dashboard n'est plus directement utilisé, mais on peut le laisser ou le supprimer.
    // Pour éviter les erreurs 404 si des liens existent, on peut le rediriger.
    @GetMapping("/dashboard")
    public String redirectToTypedDashboard(HttpSession session) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Admin) {
            return "redirect:/admin/dashboard";
        } else if (userAttribute instanceof Client) {
            return "redirect:/client/dashboard";
        } else if (userAttribute instanceof Restaurant) {
            return "redirect:/restaurant/dashboard";
        }
        return "redirect:/login"; // Si pas connecté ou type inconnu
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}