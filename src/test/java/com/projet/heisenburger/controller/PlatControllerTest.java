package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.PlatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PlatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlatRepository platRepository;

    @Mock
    private Model model;

    @InjectMocks
    private PlatController platController;

    private Restaurant testRestaurant;
    private Plat testPlat;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(platController).build();

        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setNom("Test Restaurant");

        testPlat = new Plat();
        testPlat.setId(1);
        testPlat.setNom("Burger Test");
        testPlat.setRestaurant(testRestaurant);
    }

    // Test getAuthenticatedRestaurant method (private, but its behavior is tested through public methods)
    // This method is now protected in PlatController, so it can be directly tested.
    @Test
    void getAuthenticatedRestaurant_returnsRestaurant_whenSessionHasRestaurant() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        Restaurant result = platController.getAuthenticatedRestaurant(mockHttpSession);
        assert(result.equals(testRestaurant));
    }

    @Test
    void getAuthenticatedRestaurant_returnsNull_whenSessionHasNoRestaurant() {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        Restaurant result = platController.getAuthenticatedRestaurant(mockHttpSession);
        assert(result == null);
    }

    @Test
    void getAuthenticatedRestaurant_returnsNull_whenSessionHasNonRestaurantUser() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new Object()); // Simulate a non-Restaurant object
        Restaurant result = platController.getAuthenticatedRestaurant(mockHttpSession);
        assert(result == null);
    }

    // Test listPlats method
    @Test
    void listPlats_returnsRestaurantPlatsView_whenAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.findByRestaurant(testRestaurant)).thenReturn(Arrays.asList(testPlat));

        mockMvc.perform(get("/restaurant/plats").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/restaurant_plats"))
                .andExpect(model().attributeExists("plats"))
                .andExpect(model().attribute("plats", Arrays.asList(testPlat)))
                .andExpect(model().attribute("restaurantName", testRestaurant.getNom()));

        verify(platRepository).findByRestaurant(testRestaurant);
    }

    @Test
    void listPlats_redirectsToLogin_whenNotAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        mockMvc.perform(get("/restaurant/plats").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(platRepository);
    }

    // Test showNewPlatForm method
    @Test
    void showNewPlatForm_returnsNewPlatFormView_whenAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);

        mockMvc.perform(get("/restaurant/plats/new").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/restaurant_plat_form"))
                .andExpect(model().attributeExists("plat"))
                .andExpect(model().attribute("plat", instanceOf(Plat.class)))
                .andExpect(model().attribute("pageTitle", "Ajouter un nouveau plat"));
    }

    @Test
    void showNewPlatForm_redirectsToLogin_whenNotAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        mockMvc.perform(get("/restaurant/plats/new").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // Test savePlat method
    @Test
    void savePlat_savesPlatAndRedirects_whenAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.save(any(Plat.class))).thenReturn(testPlat);

        mockMvc.perform(post("/restaurant/plats/save")
                        .session(mockHttpSession)
                        .flashAttr("plat", testPlat))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "Plat sauvegardé avec succès !"));

        verify(platRepository).save(testPlat);
        assert(testPlat.getRestaurant().equals(testRestaurant));
    }

    @Test
    void savePlat_redirectsToLogin_whenNotAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        mockMvc.perform(post("/restaurant/plats/save")
                        .session(mockHttpSession)
                        .flashAttr("plat", testPlat))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(platRepository);
    }

    // Test showEditPlatForm method
    @Test
    void showEditPlatForm_returnsEditPlatFormView_whenAuthenticatedAndAuthorized() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.findById(testPlat.getId())).thenReturn(Optional.of(testPlat));

        mockMvc.perform(get("/restaurant/plats/edit/{id}", testPlat.getId()).session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/restaurant_plat_form"))
                .andExpect(model().attributeExists("plat"))
                .andExpect(model().attribute("plat", testPlat))
                .andExpect(model().attribute("pageTitle", "Modifier le plat : " + testPlat.getNom()));

        verify(platRepository).findById(testPlat.getId());
    }

    @Test
    void showEditPlatForm_redirectsToLogin_whenNotAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        mockMvc.perform(get("/restaurant/plats/edit/{id}", testPlat.getId()).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(platRepository);
    }

    @Test
    void showEditPlatForm_redirectsWithErrorMessage_whenPlatNotFound() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurant/plats/edit/{id}", 999).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Plat non trouvé."));

        verify(platRepository).findById(999);
    }

    @Test
    void showEditPlatForm_redirectsWithErrorMessage_whenUnauthorized() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setId(2);
        Plat otherPlat = new Plat();
        otherPlat.setId(2);
        otherPlat.setRestaurant(otherRestaurant);

        when(platRepository.findById(otherPlat.getId())).thenReturn(Optional.of(otherPlat));

        mockMvc.perform(get("/restaurant/plats/edit/{id}", otherPlat.getId()).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Accès non autorisé à ce plat."));

        verify(platRepository).findById(otherPlat.getId());
    }

    // Test deletePlat method
    @Test
    void deletePlat_deletesPlatAndRedirects_whenAuthenticatedAndAuthorized() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.findById(testPlat.getId())).thenReturn(Optional.of(testPlat));
        doNothing().when(platRepository).delete(testPlat);

        mockMvc.perform(get("/restaurant/plats/delete/{id}", testPlat.getId()).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "Plat '" + testPlat.getNom() + "' supprimé avec succès !"));

        verify(platRepository).findById(testPlat.getId());
        verify(platRepository).delete(testPlat);
    }

    @Test
    void deletePlat_redirectsToLogin_whenNotAuthenticated() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession(); // Empty session
        mockMvc.perform(get("/restaurant/plats/delete/{id}", testPlat.getId()).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(platRepository);
    }

    @Test
    void deletePlat_redirectsWithErrorMessage_whenPlatNotFound() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);
        when(platRepository.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurant/plats/delete/{id}", 999).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Plat non trouvé pour la suppression."));

        verify(platRepository).findById(999);
        verify(platRepository, never()).delete(any(Plat.class));
    }

    @Test
    void deletePlat_redirectsWithErrorMessage_whenUnauthorized() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", testRestaurant);

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setId(2);
        Plat otherPlat = new Plat();
        otherPlat.setId(2);
        otherPlat.setNom("Other Plat");
        otherPlat.setRestaurant(otherRestaurant);

        when(platRepository.findById(otherPlat.getId())).thenReturn(Optional.of(otherPlat));

        mockMvc.perform(get("/restaurant/plats/delete/{id}", otherPlat.getId()).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/plats"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Accès non autorisé pour supprimer ce plat."));

        verify(platRepository).findById(otherPlat.getId());
        verify(platRepository, never()).delete(any(Plat.class));
    }
}