package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Categorie;
import com.projet.heisenburger.model.Commande;
import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.CategorieRepository;
import com.projet.heisenburger.repository.CommandeRepository;
import com.projet.heisenburger.repository.PlatRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contrôleur Spring Boot pour la gestion des fonctionnalités administratives de l'application Heisenburger.
 * Gère les opérations CRUD pour les restaurants et leurs menus (plats), ainsi que la gestion des logos
 * des restaurants et l'affichage des commandes et clients.
 */
@Controller
public class AdminController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    /**
     * Vérifie si l'utilisateur connecté est un administrateur.
     *
     * @param session La session HTTP actuelle.
     * @return L'objet Admin si l'utilisateur est un administrateur, sinon null.
     */
    private Admin getAuthenticatedAdmin(HttpSession session) {
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof Admin) {
            return (Admin) userAttribute;
        }
        return null;
    }

    /**
     * Affiche la page de gestion des restaurants pour l'administrateur.
     * Permet de lister tous les restaurants et d'afficher les détails d'un restaurant sélectionné,
     * y compris son menu.
     *
     * @param session La session HTTP.
     * @param model Le modèle pour la vue.
     * @param selectedRestaurantId L'ID du restaurant sélectionné (optionnel).
     * @return Le nom de la vue "admin/admin_gestion_restaurant" ou une redirection vers la page de connexion.
     */
    @GetMapping("/admin_gestion_restaurant")
    public String adminGestionRestaurant(HttpSession session, Model model,
                                         @RequestParam(name = "idRestaurant", required = false) Integer selectedRestaurantId) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

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

    /**
     * Permet à l'administrateur de basculer le statut (actif/inactif) d'un restaurant.
     *
     * @param idRestaurant L'ID du restaurant à modifier.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Une redirection vers la page de gestion des restaurants.
     */
    @PostMapping("/admin/restaurant/toggle-statut/{idRestaurant}")
    public String toggleRestaurantStatut(@PathVariable("idRestaurant") int idRestaurant,
                                         HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
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

    /**
     * Supprime un restaurant de la base de données.
     * Gère les exceptions de violation d'intégrité des données si le restaurant est lié à des commandes.
     *
     * @param idRestaurant L'ID du restaurant à supprimer.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Une redirection vers la page de gestion des restaurants.
     */
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

    /**
     * Affiche le formulaire de modification d'un restaurant existant.
     *
     * @param idRestaurant L'ID du restaurant à modifier.
     * @param model Le modèle pour la vue.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Le nom de la vue "admin/admin_restaurant_form" ou une redirection.
     */
    @GetMapping("/admin/restaurant/modifier/{idRestaurant}")
    public String showModifierRestaurantForm(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                           HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

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

    /**
     * Sauvegarde les modifications apportées à un restaurant.
     *
     * @param formRestaurant L'objet Restaurant avec les données du formulaire.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @param session La session HTTP.
     * @return Une redirection vers la page de gestion des restaurants.
     */
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

    /**
     * Affiche la page de gestion du menu d'un restaurant spécifique.
     *
     * @param idRestaurant L'ID du restaurant dont on veut gérer le menu.
     * @param model Le modèle pour la vue.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Le nom de la vue "admin/admin_restaurant_menu" ou une redirection.
     */
    @GetMapping("/admin/restaurant/menu/{idRestaurant}")
    public String showRestaurantMenu(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("plats", platRepository.findByRestaurant(restaurant));
            model.addAttribute("pageTitle", "Gérer le Menu de " + restaurant.getNom());
            List<Categorie> categories = categorieRepository.findAll();
            model.addAttribute("allCategories", categories);
            return "admin/admin_restaurant_menu";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
            return "redirect:/admin_gestion_restaurant";
        }
    }

    /**
     * Affiche le formulaire pour ajouter un nouveau plat au menu d'un restaurant.
     *
     * @param idRestaurant L'ID du restaurant auquel ajouter le plat.
     * @param model Le modèle pour la vue.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Le nom de la vue "admin/admin_plat_form" ou une redirection.
     */
    @GetMapping("/admin/restaurant/menu/{idRestaurant}/new")
    public String showNewPlatFormAdmin(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                       HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            model.addAttribute("restaurant", restaurantOpt.get());
            model.addAttribute("plat", new Plat());
            model.addAttribute("pageTitle", "Ajouter un nouveau plat au menu de " + restaurantOpt.get().getNom());
            List<Categorie> categories = categorieRepository.findAll();
            model.addAttribute("allCategories", categories);
            return "admin/admin_plat_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
            return "redirect:/admin_gestion_restaurant";
        }
    }

    /**
     * Sauvegarde un nouveau plat ou met à jour un plat existant pour un restaurant.
     *
     * @param idRestaurant L'ID du restaurant concerné.
     * @param plat L'objet Plat avec les données du formulaire.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @param session La session HTTP.
     * @return Une redirection vers la page de gestion du menu du restaurant.
     */
    @PostMapping("/admin/restaurant/menu/{idRestaurant}/save")
    public String savePlatAdmin(@PathVariable("idRestaurant") int idRestaurant,
                                @ModelAttribute("plat") Plat plat,
                                RedirectAttributes redirectAttributes, HttpSession session) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            plat.setRestaurant(restaurant);
            platRepository.save(plat);
            redirectAttributes.addFlashAttribute("successMessage", "Plat sauvegardé avec succès !");
            return "redirect:/admin/restaurant/menu/" + idRestaurant;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé pour sauvegarder le plat.");
            return "redirect:/admin_gestion_restaurant";
        }
    }

    /**
     * Affiche le formulaire pour modifier un plat existant du menu d'un restaurant.
     *
     * @param idRestaurant L'ID du restaurant concerné.
     * @param idPlat L'ID du plat à modifier.
     * @param model Le modèle pour la vue.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Le nom de la vue "admin/admin_plat_form" ou une redirection.
     */
    @GetMapping("/admin/restaurant/menu/{idRestaurant}/edit/{idPlat}")
    public String showEditPlatFormAdmin(@PathVariable("idRestaurant") int idRestaurant,
                                        @PathVariable("idPlat") int idPlat,
                                        Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        Optional<Plat> platOptional = platRepository.findById(idPlat);

        if (restaurantOpt.isPresent() && platOptional.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            Plat plat = platOptional.get();

            if (plat.getRestaurant().getIdRestaurant() != restaurant.getIdRestaurant()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le plat ne correspond pas à ce restaurant.");
                return "redirect:/admin/restaurant/menu/" + idRestaurant;
            }

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("plat", plat);
            model.addAttribute("pageTitle", "Modifier le plat : " + plat.getNom());
            return "admin/admin_plat_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant ou plat non trouvé.");
            return "redirect:/admin/restaurant/menu/" + idRestaurant;
        }
    }

    /**
     * Supprime un plat du menu d'un restaurant.
     *
     * @param idRestaurant L'ID du restaurant concerné.
     * @param idPlat L'ID du plat à supprimer.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @param session La session HTTP.
     * @return Une redirection vers la page de gestion du menu du restaurant.
     */
    @PostMapping("/admin/restaurant/menu/{idRestaurant}/delete/{idPlat}")
    public String deletePlatAdmin(@PathVariable("idRestaurant") int idRestaurant,
                                  @PathVariable("idPlat") int idPlat,
                                  RedirectAttributes redirectAttributes, HttpSession session) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        Optional<Plat> platOptional = platRepository.findById(idPlat);

        if (restaurantOpt.isPresent() && platOptional.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            Plat plat = platOptional.get();

            if (plat.getRestaurant().getIdRestaurant() != restaurant.getIdRestaurant()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le plat ne correspond pas à ce restaurant.");
                return "redirect:/admin/restaurant/menu/" + idRestaurant;
            }

            platRepository.delete(plat);
            redirectAttributes.addFlashAttribute("successMessage", "Plat '" + plat.getNom() + "' supprimé avec succès !");
            return "redirect:/admin/restaurant/menu/" + idRestaurant;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant ou plat non trouvé pour la suppression.");
            return "redirect:/admin/restaurant/menu/" + idRestaurant;
        }
    }

    /**
     * Affiche le formulaire pour modifier le logo d'un restaurant.
     *
     * @param idRestaurant L'ID du restaurant dont on veut modifier le logo.
     * @param model Le modèle pour la vue.
     * @param session La session HTTP.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @return Le nom de la vue "admin/admin_restaurant_logo" ou une redirection.
     */
    @GetMapping("/admin/restaurant/logo/{idRestaurant}")
    public String showLogoRestaurantForm(@PathVariable("idRestaurant") int idRestaurant, Model model,
                                          HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByIdRestaurant(idRestaurant);
        if (restaurantOpt.isPresent()) {
            model.addAttribute("restaurant", restaurantOpt.get());
            model.addAttribute("pageTitle", "Modifier le Logo: " + restaurantOpt.get().getNom());
            return "admin/admin_restaurant_logo";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé.");
            return "redirect:/admin_restaurant_logo";
        }
    }

    /**
     * Sauvegarde le logo téléchargé pour un restaurant.
     *
     * @param formRestaurant L'objet Restaurant (contient l'ID).
     * @param logoFile Le fichier image du logo téléchargé.
     * @param redirectAttributes Les attributs de redirection pour les messages flash.
     * @param session La session HTTP.
     * @return Une redirection vers la page de gestion des restaurants.
     */
    @PostMapping("/admin/restaurant/logo/save")
    public String saveRestaurantLogo(@ModelAttribute("restaurant") Restaurant formRestaurant,
                                     @RequestParam("logoFile") MultipartFile logoFile,
                                     RedirectAttributes redirectAttributes, HttpSession session) {
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }

        Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findById(formRestaurant.getId());
        if (existingRestaurantOpt.isPresent()) {
            Restaurant dbRestaurant = existingRestaurantOpt.get();

            if (!logoFile.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String originalFilename = logoFile.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(logoFile.getInputStream(), filePath);

                    dbRestaurant.setLogoUrl("/uploads/" + uniqueFileName);
                    redirectAttributes.addFlashAttribute("successMessage", "Logo du restaurant '" + dbRestaurant.getNom() + "' mis à jour !");

                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du téléchargement du logo: " + e.getMessage());
                    return "redirect:/admin/restaurant/logo/" + dbRestaurant.getIdRestaurant();
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Aucun fichier sélectionné pour le logo.");
                return "redirect:/admin/restaurant/logo/" + dbRestaurant.getIdRestaurant();
            }

            restaurantRepository.save(dbRestaurant);
            return "redirect:/admin_gestion_restaurant?idRestaurant=" + dbRestaurant.getIdRestaurant();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant non trouvé pour la mise à jour du logo.");
        }
        return "redirect:/admin_gestion_restaurant";
    }

    /**
     * Affiche la page de gestion des clients pour l'administrateur.
     * Permet de filtrer les commandes par statut.
     *
     * @param session La session HTTP.
     * @param model Le modèle pour la vue.
     * @param idClientParam L'ID du client (non utilisé actuellement, mais conservé pour la cohérence).
     * @param statutCommandeParam Le statut de la commande pour le filtrage (optionnel).
     * @return Le nom de la vue "admin/admin_gestion_client" ou une redirection.
     */
    @GetMapping("/admin_gestion_client")
    public String adminClient(HttpSession session, Model model,
                                @RequestParam(name = "idClient", required = false) String idClientParam,
                                @RequestParam(name = "statutCommande", required = false) String statutCommandeParam) {
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
        
        model.addAttribute("commandes", commandes);
        model.addAttribute("currentStatutCommande", statutCommandeParam);

        return "admin/admin_gestion_client";
    }

    /**
     * Affiche la page de gestion des commandes pour l'administrateur.
     *
     * @param session La session HTTP.
     * @param model Le modèle pour la vue.
     * @return Le nom de la vue "admin/admin_gestion_commande" ou une redirection.
     */
    @GetMapping("/admin_gestion_commande")
    public String adminGestionCommande(HttpSession session, Model model){
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        return "admin/admin_gestion_commande";
    }

    /**
     * Affiche la page de profil de l'administrateur.
     *
     * @param session La session HTTP.
     * @param model Le modèle pour la vue.
     * @return Le nom de la vue "admin/admin_profil" ou une redirection.
     */
    @GetMapping("/admin_profil")
    public String adminProfil(HttpSession session, Model model){
        Admin admin = getAuthenticatedAdmin(session);
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);
        return "admin/admin_profil";
    }

    /**
     * Affiche le tableau de bord de l'administrateur.
     * Permet de filtrer les restaurants par code postal et statut, et les commandes par statut.
     *
     * @param session La session HTTP.
     * @param model Le modèle pour la vue.
     * @param codePostal Le code postal pour filtrer les restaurants (optionnel).
     * @param statutRestaurantParam Le statut du restaurant pour le filtrage (optionnel, par défaut "actif").
     * @param statutCommandeParam Le statut de la commande pour le filtrage (optionnel, par défaut "en_livraison").
     * @return Le nom de la vue "admin/admin_dashboard" ou une redirection.
     */
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
            filteredCommandes = allCommandes;
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
            filteredRestaurants = restaurantsFromDB;
        }
        model.addAttribute("restaurants", filteredRestaurants);
        model.addAttribute("currentCodePostal", codePostal);
        model.addAttribute("currentStatutRestaurant", statutRestaurantParam);

        return "admin/admin_dashboard";
    }
}