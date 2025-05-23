package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Commande;
import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.CommandeRepository;
import com.projet.heisenburger.repository.RestaurantRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // Refactored authentication check
    private Admin getAuthenticatedAdmin(HttpSession session) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Admin) {
            return (Admin) userAttribute;
        }
        return null;
    }

    @GetMapping("/admin_gestion_restaurant")
    public String adminGestionRestaurant(HttpSession session, Model model,
                                         @RequestParam(name = "idRestaurant", required = false) Integer selectedRestaurantId) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin); // Add admin to model for the view

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        model.addAttribute("allRestaurants", allRestaurants);

        if (selectedRestaurantId != null && selectedRestaurantId > 0) {
            Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(selectedRestaurantId);
            if (restaurantOpt.isPresent()) {
                Restaurant selectedRestaurant = restaurantOpt.get();
                model.addAttribute("selectedRestaurant", selectedRestaurant);
                List<Plat> menu = selectedRestaurant.getPlats();
                model.addAttribute("selectedRestaurantMenu", menu != null ? menu : Collections.emptyList());
            } else {
                model.addAttribute("errorMessage", "Restaurant sélectionné non trouvé.");
                model.addAttribute("selectedRestaurantMenu", Collections.emptyList());
            }
        } else {
            model.addAttribute("selectedRestaurantMenu", Collections.emptyList());
        }
        return "admin/admin_gestion_restaurant";
    }

    @PostMapping("/admin/restaurant/toggle-statut/{idRestaurant}")
    public String toggleRestaurantStatut(@PathVariable("idRestaurant") int idRestaurant,
                                         HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            // Optionally add a flash attribute for unauthorized access if you want to show a message on login page
            return "redirect:/login";
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            if ("actif".equalsIgnoreCase(restaurant.getStatut())) {
                restaurant.setStatut("inactif");
            } else {
                restaurant.setStatut("actif");
            }
            restaurantRepository.save(restaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Statut du restaurant '" + restaurant.getNom() + "' modifié avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
        }
        return "redirect:/admin_gestion_restaurant?idRestaurant=" + idRestaurant;
    }

    @PostMapping("/admin/restaurant/supprimer/{idRestaurant}")
    public String supprimerRestaurant(@PathVariable("idRestaurant") int idRestaurant,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            Restaurant restaurantToDelete = restaurantOpt.get();
            try {
                restaurantRepository.delete(restaurantToDelete);
                redirectAttributes.addFlashAttribute("successMessage", "Restaurant '" + restaurantToDelete.getNom() + "' supprimé avec succès.");
            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer le restaurant '" + restaurantToDelete.getNom() + "' car il est lié à des commandes existantes.");
                return "redirect:/admin_gestion_restaurant?idRestaurant=" + idRestaurant;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du restaurant: " + e.getMessage());
                return "redirect:/admin_gestion_restaurant?idRestaurant=" + idRestaurant;
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé pour la suppression.");
        }
        return "redirect:/admin_gestion_restaurant";
    }

    @GetMapping("/admin/restaurant/modifier/{idRestaurant}")
    public String showModifierRestaurantForm(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                           HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin); // Add admin to model for the view (e.g. navbar)

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            model.addAttribute("restaurant", restaurantOpt.get());
            model.addAttribute("pageTitle", "Modifier le Restaurant: " + restaurantOpt.get().getNom());
            return "admin/admin_restaurant_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
            return "redirect:/admin_gestion_restaurant";
        }
    }

    @PostMapping("/admin/restaurant/save")
    public String saveModifiedRestaurant(@ModelAttribute("restaurant") Restaurant formRestaurant,
                                         RedirectAttributes redirectAttributes, HttpSession session) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        
        Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findById(formRestaurant.getId());
        if (existingRestaurantOpt.isPresent()) {
            Restaurant dbRestaurant = existingRestaurantOpt.get();
            dbRestaurant.setNom(formRestaurant.getNom());
            dbRestaurant.setEmail(formRestaurant.getEmail());
            dbRestaurant.setDescription(formRestaurant.getDescription());
            dbRestaurant.setAdresse(formRestaurant.getAdresse());
            dbRestaurant.setTelephone(formRestaurant.getTelephone());
            dbRestaurant.setHoraires(formRestaurant.getHoraires());
            dbRestaurant.setStatut(formRestaurant.getStatut());

            restaurantRepository.save(dbRestaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Restaurant '" + dbRestaurant.getNom() + "' modifié avec succès !");
            return "redirect:/admin_gestion_restaurant?idRestaurant=" + dbRestaurant.getIdRestaurant();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé pour la modification (ID Login: " + formRestaurant.getId() + ").");
        }
        return "redirect:/admin_gestion_restaurant";
    }

    @GetMapping("/admin/restaurant/logo/{idRestaurant}")
    public String showLogoRestaurantForm(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                          HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin); // Add admin to model for the view

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            model.addAttribute("restaurant", restaurantOpt.get());
            model.addAttribute("pageTitle", "Modifier le Logo: " + restaurantOpt.get().getNom());
            return "admin/admin_restaurant_logo_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
            return "redirect:/admin_gestion_restaurant";
        }
    }

    @PostMapping("/admin/restaurant/logo/save")
    public String saveRestaurantLogo(@ModelAttribute("restaurant") Restaurant formRestaurant,
                                     RedirectAttributes redirectAttributes, HttpSession session) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findById(formRestaurant.getId());
        if (existingRestaurantOpt.isPresent()) {
            Restaurant dbRestaurant = existingRestaurantOpt.get();
            dbRestaurant.setLogoUrl(formRestaurant.getLogoUrl());
            restaurantRepository.save(dbRestaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Logo du restaurant '" + dbRestaurant.getNom() + "' mis à jour !");
            return "redirect:/admin_gestion_restaurant?idRestaurant=" + dbRestaurant.getIdRestaurant();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé pour la mise à jour du logo.");
        }
        return "redirect:/admin_gestion_restaurant";
    }

    @GetMapping("/admin_gestion_client")
    public String adminClient(HttpSession session, Model model,
                                @RequestParam(name = "idClient", required = false) String idClientParam, // Renamed for clarity
                                @RequestParam(name = "statutCommande", required = false) String statutCommandeParam) { // Added statutCommande
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        
        List<Commande> commandes;
        if (statutCommandeParam != null && !statutCommandeParam.isEmpty()) {
            final String statutToCompare = statutCommandeParam.toUpperCase();
            commandes = commandeRepository.findAll().stream()
                .filter(c -> c.getStatut() != null && statutToCompare.equals(c.getStatut().toUpperCase()))
                .collect(Collectors.toList());
        } else {
            commandes = commandeRepository.findAll();
        }
        
        // If idClientParam is provided, further filter by client ID (if needed, not fully implemented here for brevity)
        // For now, just passing all (or status-filtered) commandes
        model.addAttribute("commandes", commandes);
        model.addAttribute("currentStatutCommande", statutCommandeParam);
        // model.addAttribute("currentIdClient", idClientParam); // If you implement client filtering

        return "admin/admin_gestion_client";
    }


    @GetMapping("/admin_gestion_commande")
    public String adminGestionCommande(HttpSession session, Model model){
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        // Add logic to fetch and display commandes if necessary
        return "admin/admin_gestion_commande";
    }

    @GetMapping("/admin_profil")
    public String adminProfil(HttpSession session, Model model){
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        return "admin/admin_profil";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model,
                                @RequestParam(name = "codePostal", required = false) String codePostal,
                                @RequestParam(name = "statutRestaurant", required = false, defaultValue = "actif") String statutRestaurantParam,
                                @RequestParam(name = "statutCommande", required = false, defaultValue = "en_livraison") String statutCommandeParam) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
                                     
        List<Commande> allCommandes = commandeRepository.findAll();
        List<Commande> filteredCommandes;
        if (statutCommandeParam != null && !statutCommandeParam.isEmpty()) {
            final String statutToCompareCmd = statutCommandeParam.toUpperCase();
            filteredCommandes = allCommandes.stream()
                    .filter(r -> r.getStatut() != null && statutToCompareCmd.equals(r.getStatut().toUpperCase()))
                    .collect(Collectors.toList());
        } else {
            filteredCommandes = allCommandes; // Or an empty list if "Tous statuts" means no filter initially
        }
        model.addAttribute("commandes", filteredCommandes);
        model.addAttribute("currentStatutCommande", statutCommandeParam);

        List<Restaurant> restaurantsFromDB;
        if (codePostal != null && !codePostal.isEmpty()) {
            restaurantsFromDB = restaurantRepository.findByAdresseContaining(codePostal);
        } else {
            restaurantsFromDB = restaurantRepository.findAll();
        }
        
        List<Restaurant> filteredRestaurants;
        if (statutRestaurantParam != null && !statutRestaurantParam.isEmpty()) {
            final String statutToCompareResto = statutRestaurantParam.toUpperCase();
            filteredRestaurants = restaurantsFromDB.stream()
                    .filter(r -> r.getStatut() != null && statutToCompareResto.equals(r.getStatut().toUpperCase()))
                    .collect(Collectors.toList());
        } else {
             filteredRestaurants = restaurantsFromDB; // Or an empty list
        }
        model.addAttribute("restaurants", filteredRestaurants);
        model.addAttribute("currentCodePostal", codePostal);
        model.addAttribute("currentStatutRestaurant", statutRestaurantParam);

        return "admin/admin_dashboard";
    }
}