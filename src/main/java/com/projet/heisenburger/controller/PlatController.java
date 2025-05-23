package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.PlatRepository; 

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/restaurant/plats") 
public class PlatController {

    @Autowired
    private PlatRepository platRepository;

    // Méthode pour vérifier si l'utilisateur est un restaurant connecté
    private Restaurant getAuthenticatedRestaurant(HttpSession session) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Restaurant) {
            return (Restaurant) userAttribute;
        }
        return null;
    }

    // GET /restaurant/plats : Lister les plats du restaurant connecté
    @GetMapping
    public String listPlats(HttpSession session, Model model) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login"; // Redirige si non connecté ou pas un restaurant
        }

        List<Plat> plats = platRepository.findByRestaurant(restaurant); 
        model.addAttribute("plats", plats);
        model.addAttribute("restaurantName", restaurant.getNom());
        return "restaurant/restaurant_plats";
    }

    // GET /restaurant/plats/new : Afficher le formulaire pour ajouter un nouveau plat
    @GetMapping("/new")
    public String showNewPlatForm(HttpSession session, Model model) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        model.addAttribute("plat", new Plat()); 
        model.addAttribute("pageTitle", "Ajouter un nouveau plat");
        return "restaurant/restaurant_plat_form"; // Vue du formulaire [cite: 66]
    }

    @PostMapping("/save")
    public String savePlat(@ModelAttribute("plat") Plat plat,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        // Associer le plat au restaurant connecté avant de sauvegarder
        plat.setRestaurant(restaurant);
        platRepository.save(plat);

        redirectAttributes.addFlashAttribute("successMessage", "Plat sauvegardé avec succès !");
        return "redirect:/restaurant/plats"; 
    }

    @GetMapping("/edit/{id}")
    public String showEditPlatForm(@PathVariable("id") int id, 
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        Optional<Plat> platOptional = platRepository.findById(id);
        if (platOptional.isPresent()) {
            Plat plat = platOptional.get();
            if (plat.getRestaurant().getId() != (restaurant.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé à ce plat.");
                return "redirect:/restaurant/plats";
            }
            model.addAttribute("plat", plat);
            model.addAttribute("pageTitle", "Modifier le plat : " + plat.getNom());
            return "restaurant/restaurant_plat_form"; 
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Plat non trouvé.");
            return "redirect:/restaurant/plats";
        }
    }

    // GET /restaurant/plats/delete/{id} : Supprimer un plat
    @GetMapping("/delete/{id}")
    public String deletePlat(@PathVariable("id") int id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        Optional<Plat> platOptional = platRepository.findById(id);
        if (platOptional.isPresent()) {
            Plat plat = platOptional.get();
            if (plat.getRestaurant().getId() == (restaurant.getId())) {
                platRepository.delete(plat); 
                redirectAttributes.addFlashAttribute("successMessage", "Plat '" + plat.getNom() + "' supprimé avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé pour supprimer ce plat.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Plat non trouvé pour la suppression.");
        }
        return "redirect:/restaurant/plats";
    }
}