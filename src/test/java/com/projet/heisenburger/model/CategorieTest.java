package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CategorieTest {

    @Test
    void testDefaultConstructor() {
        Categorie categorie = new Categorie();
        assertNotNull(categorie);
        assertEquals(0, categorie.getId());
        assertNull(categorie.getNom());
        assertNull(categorie.getDescription());
    }

    @Test
    void testParameterizedConstructor() {
        Categorie categorie = new Categorie("Dessert");
        assertNotNull(categorie);
        assertEquals(0, categorie.getId());
        assertEquals("Dessert", categorie.getNom());
        assertNull(categorie.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        Categorie categorie = new Categorie();
        categorie.setId(1);
        categorie.setNom("Boissons");
        categorie.setDescription("Soft drinks and juices");

        assertEquals(1, categorie.getId());
        assertEquals("Boissons", categorie.getNom());
        assertEquals("Soft drinks and juices", categorie.getDescription());
    }
}