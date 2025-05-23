package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.PlatRepository; // Le document utilise PlatRepository
// import com.projet.heisenburger.service.PlatService; // Si un service était utilisé

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Pour @ModelAttribute, @PathVariable etc.
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/restaurant/plats") // Toutes les routes de ce contrôleur commenceront par /restaurant/plats
public class PlatController {

    @Autowired
    private PlatRepository platRepository;
    // private PlatService platService; // Si on utilisait une couche Service

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

        List<Plat> plats = platRepository.findByRestaurant(restaurant); // Récupère les plats du restaurant [cite: 65]
        model.addAttribute("plats", plats);
        model.addAttribute("restaurantName", restaurant.getNom()); // Pour afficher le nom du restaurant
        return "restaurant/restaurant_plats"; // Vue pour lister les plats [cite: 65]
    }

    // GET /restaurant/plats/new : Afficher le formulaire pour ajouter un nouveau plat
    @GetMapping("/new")
    public String showNewPlatForm(HttpSession session, Model model) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        model.addAttribute("plat", new Plat()); // Objet plat vide pour le formulaire [cite: 66]
        model.addAttribute("pageTitle", "Ajouter un nouveau plat");
        return "restaurant/restaurant_plat_form"; // Vue du formulaire [cite: 66]
    }

    // POST /restaurant/plats/save : Sauvegarder un plat (nouveau ou modifié)
    @PostMapping("/save")
    public String savePlat(@ModelAttribute("plat") Plat plat, // @ModelAttribute lie les données du formulaire à l'objet Plat
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        // Associer le plat au restaurant connecté avant de sauvegarder
        plat.setRestaurant(restaurant); // [cite: 68]
        platRepository.save(plat); // Sauvegarde le plat (crée ou met à jour) [cite: 68]

        redirectAttributes.addFlashAttribute("successMessage", "Plat sauvegardé avec succès !");
        return "redirect:/restaurant/plats"; // Redirige vers la liste des plats [cite: 68]
    }

    // GET /restaurant/plats/edit/{id} : Afficher le formulaire pour modifier un plat existant
    @GetMapping("/edit/{id}")
    public String showEditPlatForm(@PathVariable("id") int id, // @PathVariable récupère l'ID du plat depuis l'URL
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
            // Vérifier que le plat appartient bien au restaurant connecté [cite: 69]
            if (plat.getRestaurant().getId() != (restaurant.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé à ce plat.");
                return "redirect:/restaurant/plats"; // Redirige si le plat n'appartient pas au restaurant
            }
            model.addAttribute("plat", plat);
            model.addAttribute("pageTitle", "Modifier le plat : " + plat.getNom());
            return "restaurant/restaurant_plat_form"; // Réutilise la même vue de formulaire [cite: 70]
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Plat non trouvé.");
            return "redirect:/restaurant/plats"; // Redirige si le plat n'existe pas
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
            // Vérifier que le plat appartient bien au restaurant connecté avant de supprimer [cite: 71]
            if (plat.getRestaurant().getId() == (restaurant.getId())) {
                platRepository.delete(plat); // Supprime le plat [cite: 71]
                redirectAttributes.addFlashAttribute("successMessage", "Plat '" + plat.getNom() + "' supprimé avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Accès non autorisé pour supprimer ce plat.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Plat non trouvé pour la suppression.");
        }
        return "redirect:/restaurant/plats"; // Redirige vers la liste des plats [cite: 71]
    }
}