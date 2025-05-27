package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlatTest {

    @Test
    void testDefaultConstructor() {
        Plat plat = new Plat();
        assertNotNull(plat);
        assertEquals(0, plat.getId());
        assertNull(plat.getNom());
        assertNull(plat.getDescription());
        assertEquals(0.0, plat.getPrix());
        assertNull(plat.getImageUrl());
        assertNull(plat.getDisponible());
        assertNull(plat.getRestaurant());
        assertNull(plat.getCategorie());
    }

    @Test
    void testSettersAndGetters() {
        Plat plat = new Plat();
        plat.setId(1);
        plat.setNom("Burger Classique");
        plat.setDescription("Un délicieux burger avec du fromage et des frites.");
        plat.setPrix(12.50);
        plat.setImageUrl("http://example.com/burger.jpg");
        plat.setDisponible("oui");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(10);
        plat.setRestaurant(restaurant);
        plat.setCategorie("Plat Principal");

        assertEquals(1, plat.getId());
        assertEquals("Burger Classique", plat.getNom());
        assertEquals("Un délicieux burger avec du fromage et des frites.", plat.getDescription());
        assertEquals(12.50, plat.getPrix());
        assertEquals("http://example.com/burger.jpg", plat.getImageUrl());
        assertEquals("oui", plat.getDisponible());
        assertEquals(restaurant, plat.getRestaurant());
        assertEquals("Plat Principal", plat.getCategorie());
    }

    @Test
    void testToString() {
        Plat plat = new Plat();
        plat.setId(1);
        plat.setNom("Burger Classique");
        plat.setDescription("Un délicieux burger avec du fromage et des frites.");
        plat.setPrix(12.50);
        plat.setImageUrl("http://example.com/burger.jpg");
        plat.setDisponible("oui");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(10);
        plat.setRestaurant(restaurant);
        plat.setCategorie("Plat Principal");

        String expectedToString = "Plat{id=1, nom='Burger Classique', description='Un délicieux burger avec du fromage et des frites.', prix=12.5, imageUrl='http://example.com/burger.jpg', disponible='oui', restaurant=" + restaurant.toString() + ", categorie='Plat Principal'}";
        assertEquals(expectedToString, plat.toString());
    }
}