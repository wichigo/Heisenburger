package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Client;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.model.User;
import com.projet.heisenburger.repository.RestaurantRepository;
import com.projet.heisenburger.repository.UserRepository;
import com.projet.heisenburger.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;


    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    @GetMapping("/client_inscription")
    public String clientRegisterForm(Model model) { 
        model.addAttribute("client", new Client()); 
        return "/client/client_inscription";
    }

    @PostMapping("/client/register")
    public String registerClient(@ModelAttribute("client") Client client,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (client.getPassword() == null || client.getPassword().isEmpty() || !client.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas ou sont vides.");
            return "/client/client_inscription";
        }

        Optional<User> existingUser = userRepository.findByEmail(client.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "Un compte existe déjà avec cet email.");
            return "/client/client_inscription";
        }

        client.setStatut("actif"); // Les clients sont actifs par défaut à l'inscription
        client.setDateInscription(LocalDateTime.now());

        try {
            User savedClient = userRepository.save(client);

            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie " +
                    ((Client) savedClient).getPrenom() + "! Vous pouvez maintenant vous connecter.");
            return "redirect:/login"; // Rediriger vers la page de connexion après une inscription réussie

        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue lors de l'inscription: " + e.getMessage());
            // model.addAttribute("client", client);
            return "/client/client_inscription";
        }
    }


    @GetMapping("/restaurant_inscription")
    public String proRegisterForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "/restaurant/restaurant_inscription";
    }

    @PostMapping("/restaurant/register")
    public String registerRestaurant(@ModelAttribute("restaurant") Restaurant restaurant,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        if (restaurant.getPassword() == null || restaurant.getPassword().isEmpty() || !restaurant.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas ou sont vides.");
            return "/restaurant/restaurant_inscription";
        }
        Optional<User> existingUser = userRepository.findByEmail(restaurant.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "Un compte existe déjà avec cet email.");
            return "/restaurant/restaurant_inscription";
        }
        restaurant.setStatut("inactif");
        restaurant.setDateInscription(LocalDateTime.now());
        try {
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Inscription réussie pour le restaurant " + savedRestaurant.getNom() + "! Votre compte est en attente d'activation.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue lors de l'inscription: " + e.getMessage());
            return "/restaurant/restaurant_inscription";
        }
    }


    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model, // Ajouté pour pouvoir utiliser redirectAttributes dans les autres méthodes
                        RedirectAttributes redirectAttributes) { // Ajouté
        if (authService.authenticate(email, password)) {
            User user = authService.findUser(email);
            session.setAttribute("user", user);

            if (user instanceof Admin) {
                return "redirect:/admin/dashboard";
            } else if (user instanceof Client) {
                return "redirect:/client/dashboard";
            } else if (user instanceof Restaurant) {
                Restaurant resto = (Restaurant) user;
                if ("actif".equalsIgnoreCase(resto.getStatut())) {
                    return "redirect:/restaurant/dashboard";
                } else {
                    session.invalidate();
                    redirectAttributes.addFlashAttribute("error", "Votre compte restaurant est '" + (resto.getStatut() != null ? resto.getStatut() : "inconnu") + "' et n'est pas encore activé. Veuillez contacter l'administrateur.");
                    return "redirect:/login";
                }
            }
            model.addAttribute("error", "Type d'utilisateur inconnu !");
            return "login";
        } else {
            model.addAttribute("error", "Identifiants invalides !");
            return "login";
        }
    }


    // Tableau de bord Client
    @GetMapping("/client/dashboard")
    public String clientDashboard(HttpSession session, Model model) {
        Object userAttribute = session.getAttribute("user");
        if (!(userAttribute instanceof Client)) {
            return "redirect:/login";
        }
        model.addAttribute("user", userAttribute);
        return "client/client_dashboard";
    }

    // Tableau de bord Restaurant
    @GetMapping("/restaurant/dashboard")
    public String restaurantDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) { // Ajouté RedirectAttributes
        Object userAttribute = session.getAttribute("user");
        if (!(userAttribute instanceof Restaurant)) {
            return "redirect:/login";
        }
        Restaurant restaurant = (Restaurant) userAttribute;
         if (!"actif".equalsIgnoreCase(restaurant.getStatut())) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Votre compte restaurant est '" + (restaurant.getStatut() != null ? restaurant.getStatut() : "inconnu") + "' et n'est pas encore activé.");
            return "redirect:/login";
        }
        model.addAttribute("user", restaurant);
        return "restaurant/restaurant_dashboard";
    }

    @GetMapping("/dashboard")
    public String redirectToTypedDashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Admin) {
            return "redirect:/admin/dashboard";
        } else if (userAttribute instanceof Client) {
            return "redirect:/client/dashboard";
        } else if (userAttribute instanceof Restaurant) {
             Restaurant resto = (Restaurant) userAttribute;
            if ("actif".equalsIgnoreCase(resto.getStatut())) {
                return "redirect:/restaurant/dashboard";
            } else {
                session.invalidate();
                redirectAttributes.addFlashAttribute("error", "Votre compte restaurant est '" + (resto.getStatut() != null ? resto.getStatut() : "inconnu") + "' et n'est pas encore activé.");
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}