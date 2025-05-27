package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    @Test
    void testDefaultConstructor() {
        Restaurant restaurant = new Restaurant();
        assertNotNull(restaurant);
        assertEquals(0, restaurant.getIdRestaurant());
        assertNull(restaurant.getNom());
        assertNull(restaurant.getDescription());
        assertNull(restaurant.getAdresse());
        assertNull(restaurant.getTelephone());
        assertNull(restaurant.getLogoUrl());
        assertNull(restaurant.getStatut());
        assertNull(restaurant.getDateInscription());
        assertNull(restaurant.getHoraires());
        assertNotNull(restaurant.getPlats());
        assertTrue(restaurant.getPlats().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        String email = "restaurant@example.com";
        String password = "restopass";
        String nom = "Burger Palace";
        String description = "Best burgers in town";
        String adresse = "123 Burger Lane";
        String telephone = "0987654321";
        String logoUrl = "http://example.com/logo.png";
        String statut = "actif";
        LocalDateTime now = LocalDateTime.now();
        String horaires = "Mon-Fri 9-5";

        Restaurant restaurant = new Restaurant(email, password, nom, description, adresse, telephone, logoUrl, statut, now, horaires);
        assertNotNull(restaurant);
        assertEquals(email, restaurant.getEmail());
        assertEquals(password, restaurant.getPassword());
        assertEquals(nom, restaurant.getNom());
        assertEquals(description, restaurant.getDescription());
        assertEquals(adresse, restaurant.getAdresse());
        assertEquals(telephone, restaurant.getTelephone());
        assertEquals(logoUrl, restaurant.getLogoUrl());
        assertEquals(statut, restaurant.getStatut());
        assertEquals(now, restaurant.getDateInscription());
        assertEquals(horaires, restaurant.getHoraires());
        assertNotNull(restaurant.getPlats());
        assertTrue(restaurant.getPlats().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Pizza Place");
        restaurant.setDescription("Delicious pizzas");
        restaurant.setAdresse("456 Pizza St");
        restaurant.setTelephone("1122334455");
        restaurant.setLogoUrl("http://example.com/pizza.png");
        restaurant.setStatut("inactif");
        LocalDateTime now = LocalDateTime.now();
        restaurant.setDateInscription(now);
        restaurant.setHoraires("Sat-Sun 10-10");

        Plat plat1 = new Plat();
        plat1.setId(1);
        Plat plat2 = new Plat();
        plat2.setId(2);
        List<Plat> plats = new ArrayList<>();
        plats.add(plat1);
        plats.add(plat2);
        restaurant.setPlats(plats);

        assertEquals(1, restaurant.getIdRestaurant());
        assertEquals("Pizza Place", restaurant.getNom());
        assertEquals("Delicious pizzas", restaurant.getDescription());
        assertEquals("456 Pizza St", restaurant.getAdresse());
        assertEquals("1122334455", restaurant.getTelephone());
        assertEquals("http://example.com/pizza.png", restaurant.getLogoUrl());
        assertEquals("inactif", restaurant.getStatut());
        assertEquals(now, restaurant.getDateInscription());
        assertEquals("Sat-Sun 10-10", restaurant.getHoraires());
        assertEquals(2, restaurant.getPlats().size());
        assertTrue(restaurant.getPlats().contains(plat1));
        assertTrue(restaurant.getPlats().contains(plat2));
    }

    @Test
    void testToString() {
        Restaurant restaurant = new Restaurant();
        restaurant.setIdRestaurant(1);
        restaurant.setNom("Test Restaurant");
        restaurant.setDescription("Test Description");
        restaurant.setAdresse("Test Address");
        restaurant.setTelephone("1234567890");
        restaurant.setLogoUrl("test.png");
        restaurant.setStatut("actif");
        LocalDateTime now = LocalDateTime.now();
        restaurant.setDateInscription(now);
        restaurant.setHoraires("9am-5pm");

        String expectedToString = "Restaurant [id_restaurant=1, nom=Test Restaurant, description=Test Description, adresse=Test Address, telephone=1234567890, logoUrl=test.png, statut=actif, date_inscription=" + now + ", horaires=9am-5pm]";
        assertEquals(expectedToString, restaurant.toString());
    }
}