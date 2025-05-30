package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Commande;
import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.CommandeRepository;
import com.projet.heisenburger.repository.PlatRepository;
import com.projet.heisenburger.repository.RestaurantRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic; 
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private PlatRepository platRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAdmin = new Admin("admin@example.com", "adminpass", "ADMIN", "Test", "Admin");
        testAdmin.setId(1);
    }

    private void setupAuthenticatedAdmin() {
        when(session.getAttribute("user")).thenReturn(testAdmin);
    }

    @Test
    void adminGestionRestaurant_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        List<Restaurant> restaurants = Arrays.asList(new Restaurant(), new Restaurant());
        when(restaurantRepository.findAll()).thenReturn(restaurants);

        String viewName = adminController.adminGestionRestaurant(session, model, null);

        assertEquals("admin/admin_gestion_restaurant", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("allRestaurants", restaurants);
        verify(model).addAttribute("selectedRestaurantMenu", Collections.emptyList());
    }

    @Test
    void adminGestionRestaurant_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.adminGestionRestaurant(session, model, null);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void adminGestionRestaurant_withSelectedRestaurant_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        restaurant.setPlats(Arrays.asList(new Plat(), new Plat()));
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.findAll()).thenReturn(Collections.emptyList());

        String viewName = adminController.adminGestionRestaurant(session, model, 1);

        assertEquals("admin/admin_gestion_restaurant", viewName);
        verify(model).addAttribute("selectedRestaurant", restaurant);
        verify(model).addAttribute("selectedRestaurantMenu", restaurant.getPlats());
    }

    @Test
    void toggleRestaurantStatut_authenticatedAdmin_togglesStatutAndRedirects() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        restaurant.setStatut("actif");
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.toggleRestaurantStatut(1, session, redirectAttributes);

        assertEquals("redirect:/admin_gestion_restaurant?idRestaurant=1", viewName);
        assertEquals("inactif", restaurant.getStatut());
        verify(restaurantRepository).save(restaurant);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void toggleRestaurantStatut_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.toggleRestaurantStatut(1, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void supprimerRestaurant_authenticatedAdmin_deletesRestaurantAndRedirects() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.supprimerRestaurant(1, session, redirectAttributes);

        assertEquals("redirect:/admin_gestion_restaurant", viewName);
        verify(restaurantRepository).delete(restaurant);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void supprimerRestaurant_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.supprimerRestaurant(1, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void showModifierRestaurantForm_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.showModifierRestaurantForm(1, model, session, redirectAttributes);

        assertEquals("admin/admin_restaurant_form", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("restaurant", restaurant);
        verify(model).addAttribute(eq("pageTitle"), anyString());
    }

    @Test
    void showModifierRestaurantForm_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.showModifierRestaurantForm(1, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void saveModifiedRestaurant_authenticatedAdmin_savesRestaurantAndRedirects() {
        setupAuthenticatedAdmin();
        Restaurant formRestaurant = new Restaurant();
        formRestaurant.setId(1);
        formRestaurant.setNom("Updated Name");
        formRestaurant.setEmail("updated@example.com");
        formRestaurant.setDescription("Updated Description");
        formRestaurant.setAdresse("Updated Address");
        formRestaurant.setTelephone("1234567890");
        formRestaurant.setHoraires("9-5");
        formRestaurant.setStatut("inactif");

        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(1);
        existingRestaurant.setIdRestaurant(100);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(existingRestaurant));

        String viewName = adminController.saveModifiedRestaurant(formRestaurant, redirectAttributes, session);

        assertEquals("redirect:/admin_gestion_restaurant?idRestaurant=100", viewName);
        assertEquals("Updated Name", existingRestaurant.getNom());
        assertEquals("updated@example.com", existingRestaurant.getEmail());
        assertEquals("Updated Description", existingRestaurant.getDescription());
        assertEquals("Updated Address", existingRestaurant.getAdresse());
        assertEquals("1234567890", existingRestaurant.getTelephone());
        assertEquals("9-5", existingRestaurant.getHoraires());
        assertEquals("inactif", existingRestaurant.getStatut());
        verify(restaurantRepository).save(existingRestaurant);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void saveModifiedRestaurant_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);
        Restaurant formRestaurant = new Restaurant();

        String viewName = adminController.saveModifiedRestaurant(formRestaurant, redirectAttributes, session);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void showLogoRestaurantForm_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.showLogoRestaurantForm(1, model, session, redirectAttributes);

        assertEquals("admin/admin_restaurant_logo", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("restaurant", restaurant);
        verify(model).addAttribute(eq("pageTitle"), anyString());
    }

    @Test
    void showLogoRestaurantForm_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.showLogoRestaurantForm(1, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void saveRestaurantLogo_authenticatedAdmin_savesLogoAndRedirects() throws IOException {
        setupAuthenticatedAdmin();
        Restaurant formRestaurant = new Restaurant();
        formRestaurant.setId(1);

        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(1);
        existingRestaurant.setIdRestaurant(100);
        existingRestaurant.setNom("Test Restaurant");

        MockMultipartFile mockFile = new MockMultipartFile(
                "logoFile",
                "test-logo.png",
                "image/png",
                "test image content".getBytes()
        );

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);
            mockedFiles.when(() -> Files.copy(any(java.io.InputStream.class), any(Path.class))).thenReturn(1L);

            when(restaurantRepository.findById(1)).thenReturn(Optional.of(existingRestaurant));

            String viewName = adminController.saveRestaurantLogo(formRestaurant, mockFile, redirectAttributes, session);

            assertEquals("redirect:/admin_gestion_restaurant?idRestaurant=100", viewName);
            assertNotNull(existingRestaurant.getLogoUrl());
            verify(restaurantRepository).save(existingRestaurant);
            verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
            mockedFiles.verify(() -> Files.exists(any(Path.class)));
            mockedFiles.verify(() -> Files.createDirectories(any(Path.class)));
            mockedFiles.verify(() -> Files.copy(any(java.io.InputStream.class), any(Path.class)));
        }
    }

    @Test
    void saveRestaurantLogo_authenticatedAdmin_noFileUploaded() throws IOException {
        setupAuthenticatedAdmin();
        Restaurant formRestaurant = new Restaurant();
        formRestaurant.setId(1);

        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(1);
        existingRestaurant.setIdRestaurant(100);
        existingRestaurant.setNom("Test Restaurant");
        existingRestaurant.setLogoUrl("/existing/logo.png");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "logoFile",
                "",
                "image/png",
                new byte[0]
        );

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(existingRestaurant));

        String viewName = adminController.saveRestaurantLogo(formRestaurant, emptyFile, redirectAttributes, session);

        assertEquals("redirect:/admin/restaurant/logo/100", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void saveRestaurantLogo_unauthenticatedAdmin_redirectsToLogin() throws IOException {
        when(session.getAttribute("user")).thenReturn(null);
        Restaurant formRestaurant = new Restaurant();
        MockMultipartFile emptyFile = new MockMultipartFile(
                "logoFile",
                "",
                "image/png",
                new byte[0]
        );

        String viewName = adminController.saveRestaurantLogo(formRestaurant, emptyFile, redirectAttributes, session);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void showRestaurantMenu_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        List<Plat> plats = Arrays.asList(new Plat(), new Plat());
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));
        when(platRepository.findByRestaurant(restaurant)).thenReturn(plats);

        String viewName = adminController.showRestaurantMenu(1, model, session, redirectAttributes);

        assertEquals("admin/admin_restaurant_menu", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("restaurant", restaurant);
        verify(model).addAttribute("plats", plats);
        verify(model).addAttribute(eq("pageTitle"), anyString());
    }

    @Test
    void showRestaurantMenu_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.showRestaurantMenu(1, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void showNewPlatFormAdmin_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.showNewPlatFormAdmin(1, model, session, redirectAttributes);

        assertEquals("admin/admin_plat_form", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("restaurant", restaurant);
        verify(model).addAttribute(eq("plat"), any(Plat.class));
        verify(model).addAttribute(eq("pageTitle"), anyString());
    }

    @Test
    void showNewPlatFormAdmin_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.showNewPlatFormAdmin(1, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void savePlatAdmin_authenticatedAdmin_savesPlatAndRedirects() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        Plat plat = mock(Plat.class);
        when(plat.getNom()).thenReturn("New Plat"); 

        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));

        String viewName = adminController.savePlatAdmin(1, plat, redirectAttributes, session);

        assertEquals("redirect:/admin/restaurant/menu/1", viewName);
        verify(plat).setRestaurant(restaurant);
        verify(platRepository).save(plat);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void savePlatAdmin_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);
        Plat plat = new Plat();

        String viewName = adminController.savePlatAdmin(1, plat, redirectAttributes, session);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(platRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void showEditPlatFormAdmin_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        Plat plat = new Plat();
        plat.setId(10);
        plat.setNom("Existing Plat");
        plat.setRestaurant(restaurant);

        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));
        when(platRepository.findById(10)).thenReturn(Optional.of(plat));

        String viewName = adminController.showEditPlatFormAdmin(1, 10, model, session, redirectAttributes);

        assertEquals("admin/admin_plat_form", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("restaurant", restaurant);
        verify(model).addAttribute("plat", plat);
        verify(model).addAttribute(eq("pageTitle"), anyString());
    }

    @Test
    void showEditPlatFormAdmin_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.showEditPlatFormAdmin(1, 10, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void deletePlatAdmin_authenticatedAdmin_deletesPlatAndRedirects() {
        setupAuthenticatedAdmin();
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        Plat plat = new Plat();
        plat.setId(10);
        plat.setNom("Plat to Delete");
        plat.setRestaurant(restaurant);

        when(restaurantRepository.findByIdRestaurant(1)).thenReturn(Optional.of(restaurant));
        when(platRepository.findById(10)).thenReturn(Optional.of(plat));

        String viewName = adminController.deletePlatAdmin(1, 10, redirectAttributes, session);

        assertEquals("redirect:/admin/restaurant/menu/1", viewName);
        verify(platRepository).delete(plat);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void deletePlatAdmin_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.deletePlatAdmin(1, 10, redirectAttributes, session);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(platRepository);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void adminClient_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        List<Commande> commandes = Arrays.asList(new Commande(), new Commande());
        when(commandeRepository.findAll()).thenReturn(commandes);

        String viewName = adminController.adminClient(session, model, null, null);

        assertEquals("admin/admin_gestion_client", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("commandes", commandes);
        verify(model).addAttribute("currentStatutCommande", null);
    }

    @Test
    void adminClient_authenticatedAdmin_filtersByStatutCommande() {
        setupAuthenticatedAdmin();
        Commande commande1 = new Commande();
        commande1.setStatut("EN_LIVRAISON");
        Commande commande2 = new Commande();
        commande2.setStatut("TERMINE");
        List<Commande> allCommandes = Arrays.asList(commande1, commande2);
        when(commandeRepository.findAll()).thenReturn(allCommandes);

        String viewName = adminController.adminClient(session, model, null, "en_livraison");

        assertEquals("admin/admin_gestion_client", viewName);
        verify(model).addAttribute("commandes", Collections.singletonList(commande1));
        verify(model).addAttribute("currentStatutCommande", "en_livraison");
    }

    @Test
    void adminClient_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.adminClient(session, model, null, null);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void adminGestionCommande_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();

        String viewName = adminController.adminGestionCommande(session, model);

        assertEquals("admin/admin_gestion_commande", viewName);
        verify(model).addAttribute("user", testAdmin);
    }

    @Test
    void adminGestionCommande_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.adminGestionCommande(session, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void adminProfil_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();

        String viewName = adminController.adminProfil(session, model);

        assertEquals("admin/admin_profil", viewName);
        verify(model).addAttribute("user", testAdmin);
    }

    @Test
    void adminProfil_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.adminProfil(session, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void adminDashboard_authenticatedAdmin_returnsViewAndAddsAttributes() {
        setupAuthenticatedAdmin();
        Commande commande1 = new Commande();
        commande1.setStatut("EN_LIVRAISON");
        Commande commande2 = new Commande();
        commande2.setStatut("TERMINE");
        List<Commande> allCommandes = Arrays.asList(commande1, commande2);
        when(commandeRepository.findAll()).thenReturn(allCommandes);

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setStatut("actif");
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setStatut("inactif");
        List<Restaurant> allRestaurants = Arrays.asList(restaurant1, restaurant2);
        when(restaurantRepository.findAll()).thenReturn(allRestaurants);

        String viewName = adminController.adminDashboard(session, model, null, "actif", "en_livraison");

        assertEquals("admin/admin_dashboard", viewName);
        verify(model).addAttribute("user", testAdmin);
        verify(model).addAttribute("commandes", Collections.singletonList(commande1));
        verify(model).addAttribute("currentStatutCommande", "en_livraison");
        verify(model).addAttribute("restaurants", Collections.singletonList(restaurant1));
        verify(model).addAttribute("currentCodePostal", null);
        verify(model).addAttribute("currentStatutRestaurant", "actif");
    }

    @Test
    void adminDashboard_authenticatedAdmin_filtersByStatutCommandeAndCodePostal() {
        setupAuthenticatedAdmin();
        Commande commande1 = new Commande();
        commande1.setStatut("EN_LIVRAISON");
        Commande commande2 = new Commande();
        commande2.setStatut("TERMINE");
        List<Commande> allCommandes = Arrays.asList(commande1, commande2);
        when(commandeRepository.findAll()).thenReturn(allCommandes);

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setAdresse("123 Main St, 75001 Paris");
        restaurant1.setStatut("actif");
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setAdresse("456 Other St, 13001 Marseille");
        restaurant2.setStatut("inactif");
        List<Restaurant> allRestaurants = Arrays.asList(restaurant1, restaurant2);
        when(restaurantRepository.findAll()).thenReturn(allRestaurants);
        when(restaurantRepository.findByAdresseContaining("75001")).thenReturn(Collections.singletonList(restaurant1));


        String viewName = adminController.adminDashboard(session, model, "75001", "actif", "en_livraison");

        assertEquals("admin/admin_dashboard", viewName);
        verify(model).addAttribute("commandes", Collections.singletonList(commande1));
        verify(model).addAttribute("currentStatutCommande", "en_livraison");
        verify(model).addAttribute("restaurants", Collections.singletonList(restaurant1));
        verify(model).addAttribute("currentCodePostal", "75001");
        verify(model).addAttribute("currentStatutRestaurant", "actif");
    }

    @Test
    void adminDashboard_unauthenticatedAdmin_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = adminController.adminDashboard(session, model, null, null, null);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }
}