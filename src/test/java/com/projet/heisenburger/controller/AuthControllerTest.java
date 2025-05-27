package com.projet.heisenburger.controller;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Client;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.model.User;
import com.projet.heisenburger.repository.CommandeRepository;
import com.projet.heisenburger.repository.RestaurantRepository;
import com.projet.heisenburger.repository.UserRepository;
import com.projet.heisenburger.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private CommandeRepository commandeRepository; // Although not directly used in AuthController's core logic, it's autowired.

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_returnsHomeView() {
        String viewName = authController.home();
        assertEquals("home", viewName);
    }

    @Test
    void loginForm_returnsLoginView() {
        String viewName = authController.loginForm(model);
        assertEquals("login", viewName);
    }

    @Test
    void clientRegisterForm_returnsClientInscriptionViewAndAddsAttribute() {
        String viewName = authController.clientRegisterForm(model);
        assertEquals("/client/client_inscription", viewName);
        verify(model).addAttribute(eq("client"), any(Client.class));
    }

    @Test
    void registerClient_passwordsMismatch_returnsError() {
        Client client = new Client("test@example.com", "pass1", "1234567890", "Test", "Client", "123 Main St", "City");
        String confirmPassword = "pass2";

        String viewName = authController.registerClient(client, confirmPassword, model, redirectAttributes);

        assertEquals("/client/client_inscription", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verifyNoInteractions(userRepository);
    }

    @Test
    void registerClient_emailAlreadyExists_returnsError() {
        Client client = new Client("test@example.com", "password", "1234567890", "Test", "Client", "123 Main St", "City");
        String confirmPassword = "password";
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(new Client()));

        String viewName = authController.registerClient(client, confirmPassword, model, redirectAttributes);

        assertEquals("/client/client_inscription", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(userRepository, never()).save(any(Client.class));
    }

    @Test
    void registerClient_successfulRegistration_redirectsToLogin() {
        Client client = mock(Client.class);
        when(client.getEmail()).thenReturn("new@example.com");
        when(client.getPassword()).thenReturn("password");
        when(client.getPrenom()).thenReturn("New");
        String confirmPassword = "password";
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(Client.class))).thenReturn(client);

        String viewName = authController.registerClient(client, confirmPassword, model, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(client).setStatut("actif");
        verify(client).setDateInscription(any(LocalDateTime.class));
        verify(userRepository).save(client);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void restaurantRegisterForm_returnsRestaurantInscriptionViewAndAddsAttribute() {
        String viewName = authController.proRegisterForm(model);
        assertEquals("/restaurant/restaurant_inscription", viewName);
        verify(model).addAttribute(eq("restaurant"), any(Restaurant.class));
    }

    @Test
    void registerRestaurant_passwordsMismatch_returnsError() {
        Restaurant restaurant = new Restaurant("resto@example.com", "pass1", "Resto Test", "Desc", "Addr", "123", "logo.png", "inactif", null, "H");
        String confirmPassword = "pass2";

        String viewName = authController.registerRestaurant(restaurant, confirmPassword, model, redirectAttributes);

        assertEquals("/restaurant/restaurant_inscription", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verifyNoInteractions(userRepository);
    }

    @Test
    void registerRestaurant_emailAlreadyExists_returnsError() {
        Restaurant restaurant = new Restaurant("resto@example.com", "password", "Resto Test", "Desc", "Addr", "123", "logo.png", "inactif", null, "H");
        String confirmPassword = "password";
        when(userRepository.findByEmail(restaurant.getEmail())).thenReturn(Optional.of(new Restaurant()));

        String viewName = authController.registerRestaurant(restaurant, confirmPassword, model, redirectAttributes);

        assertEquals("/restaurant/restaurant_inscription", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void registerRestaurant_successfulRegistration_redirectsToLogin() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getEmail()).thenReturn("newresto@example.com");
        when(restaurant.getPassword()).thenReturn("password");
        when(restaurant.getNom()).thenReturn("New Resto");
        String confirmPassword = "password";
        when(userRepository.findByEmail(restaurant.getEmail())).thenReturn(Optional.empty());
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        String viewName = authController.registerRestaurant(restaurant, confirmPassword, model, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(restaurant).setStatut("inactif");
        verify(restaurant).setDateInscription(any(LocalDateTime.class));
        verify(restaurantRepository).save(restaurant);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void login_successfulAdminLogin_redirectsToAdminDashboard() {
        Admin admin = new Admin("admin@example.com", "pass", "ADMIN", "Test", "Admin");
        when(authService.authenticate("admin@example.com", "pass")).thenReturn(true);
        when(authService.findUser("admin@example.com")).thenReturn(admin);

        String viewName = authController.login("admin@example.com", "pass", session, model, redirectAttributes);

        assertEquals("redirect:/admin/dashboard", viewName);
        verify(session).setAttribute("user", admin);
    }

    @Test
    void login_successfulClientLogin_redirectsToClientDashboard() {
        Client client = new Client("client@example.com", "pass", "1112223333", "Test", "Client", "123 Client St", "Clientville");
        when(authService.authenticate("client@example.com", "pass")).thenReturn(true);
        when(authService.findUser("client@example.com")).thenReturn(client);

        String viewName = authController.login("client@example.com", "pass", session, model, redirectAttributes);

        assertEquals("redirect:/client/dashboard", viewName);
        verify(session).setAttribute("user", client);
    }

    @Test
    void login_successfulActiveRestaurantLogin_redirectsToRestaurantDashboard() {
        Restaurant restaurant = new Restaurant("resto@example.com", "pass", "Resto", "Desc", "Addr", "123", "logo.png", "actif", null, "H");
        when(authService.authenticate("resto@example.com", "pass")).thenReturn(true);
        when(authService.findUser("resto@example.com")).thenReturn(restaurant);

        String viewName = authController.login("resto@example.com", "pass", session, model, redirectAttributes);

        assertEquals("redirect:/restaurant/dashboard", viewName);
        verify(session).setAttribute("user", restaurant);
    }

    @Test
    void login_inactiveRestaurantLogin_redirectsToLoginWithError() {
        Restaurant restaurant = new Restaurant("resto@example.com", "pass", "Resto", "Desc", "Addr", "123", "logo.png", "inactif", null, "H");
        when(authService.authenticate("resto@example.com", "pass")).thenReturn(true);
        when(authService.findUser("resto@example.com")).thenReturn(restaurant);

        String viewName = authController.login("resto@example.com", "pass", session, model, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void login_invalidCredentials_returnsLoginWithError() {
        when(authService.authenticate("wrong@example.com", "wrongpass")).thenReturn(false);

        String viewName = authController.login("wrong@example.com", "wrongpass", session, model, redirectAttributes);

        assertEquals("login", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verifyNoInteractions(session);
    }

    @Test
    void clientDashboard_authenticatedClient_returnsView() {
        Client client = new Client();
        when(session.getAttribute("user")).thenReturn(client);

        String viewName = authController.clientDashboard(session, model);

        assertEquals("client/client_dashboard", viewName);
        verify(model).addAttribute("user", client);
    }

    @Test
    void clientDashboard_unauthenticated_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = authController.clientDashboard(session, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void restaurantDashboard_authenticatedActiveRestaurant_returnsView() {
        Restaurant restaurant = new Restaurant();
        restaurant.setStatut("actif");
        when(session.getAttribute("user")).thenReturn(restaurant);

        String viewName = authController.restaurantDashboard(session, model, redirectAttributes);

        assertEquals("restaurant/restaurant_dashboard", viewName);
        verify(model).addAttribute("user", restaurant);
    }

    @Test
    void restaurantDashboard_authenticatedInactiveRestaurant_redirectsToLogin() {
        Restaurant restaurant = new Restaurant();
        restaurant.setStatut("inactif");
        when(session.getAttribute("user")).thenReturn(restaurant);

        String viewName = authController.restaurantDashboard(session, model, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void restaurantDashboard_unauthenticated_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = authController.restaurantDashboard(session, model, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
        verifyNoInteractions(redirectAttributes);
    }

    @Test
    void redirectToTypedDashboard_adminUser_redirectsToAdminDashboard() {
        Admin admin = new Admin();
        when(session.getAttribute("user")).thenReturn(admin);

        String viewName = authController.redirectToTypedDashboard(session, redirectAttributes);

        assertEquals("redirect:/admin/dashboard", viewName);
    }

    @Test
    void redirectToTypedDashboard_clientUser_redirectsToClientDashboard() {
        Client client = new Client();
        when(session.getAttribute("user")).thenReturn(client);

        String viewName = authController.redirectToTypedDashboard(session, redirectAttributes);

        assertEquals("redirect:/client/dashboard", viewName);
    }

    @Test
    void redirectToTypedDashboard_activeRestaurantUser_redirectsToRestaurantDashboard() {
        Restaurant restaurant = new Restaurant();
        restaurant.setStatut("actif");
        when(session.getAttribute("user")).thenReturn(restaurant);

        String viewName = authController.redirectToTypedDashboard(session, redirectAttributes);

        assertEquals("redirect:/restaurant/dashboard", viewName);
    }

    @Test
    void redirectToTypedDashboard_inactiveRestaurantUser_redirectsToLogin() {
        Restaurant restaurant = new Restaurant();
        restaurant.setStatut("inactif");
        when(session.getAttribute("user")).thenReturn(restaurant);

        String viewName = authController.redirectToTypedDashboard(session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void redirectToTypedDashboard_noUserInSession_redirectsToLogin() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = authController.redirectToTypedDashboard(session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    void logout_invalidatesSessionAndRedirectsToLogin() {
        String viewName = authController.logout(session);

        assertEquals("redirect:/login", viewName);
        verify(session).invalidate();
    }
}