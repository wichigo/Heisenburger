package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Categorie;
import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.PlatRepository; 
import com.projet.heisenburger.repository.CategorieRepository;

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
    private CategorieRepository categorieRepository;

    @Autowired
    private PlatRepository platRepository;

    protected Restaurant getAuthenticatedRestaurant(HttpSession session) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Restaurant) {
            return (Restaurant) userAttribute;
        }
        return null;
    }

    @GetMapping
    public String listPlats(HttpSession session, Model model) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

        List<Plat> plats = platRepository.findByRestaurant(restaurant); 
        model.addAttribute("plats", plats);
        model.addAttribute("restaurantName", restaurant.getNom());
        return "restaurant/restaurant_plats";
    }

    @GetMapping("/new")
    public String showNewPlatForm(HttpSession session, Model model) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }
        model.addAttribute("plat", new Plat());
        List<Categorie> categories = categorieRepository.findAll();
        model.addAttribute("allCategories", categories);    
        model.addAttribute("pageTitle", "Ajouter un nouveau plat");
        return "restaurant/restaurant_plat_form";
    }

    @PostMapping("/save")
    public String savePlat(@ModelAttribute("plat") Plat plat,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Restaurant restaurant = getAuthenticatedRestaurant(session);
        if (restaurant == null) {
            return "redirect:/login";
        }

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
            List<Categorie> categories = categorieRepository.findAll();
            model.addAttribute("allCategories", categories);         
            model.addAttribute("pageTitle", "Modifier le plat : " + plat.getNom());
            return "restaurant/restaurant_plat_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Plat non trouvé.");
            return "redirect:/restaurant/plats";
        }
    }

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