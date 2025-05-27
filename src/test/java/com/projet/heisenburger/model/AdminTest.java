package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNotNull(admin);
        assertNull(admin.getEmail());
        assertNull(admin.getPassword());
        assertNull(admin.getRoleAdmin());
        assertNull(admin.getNom());
        assertNull(admin.getPrenom());
    }

    @Test
    void testParameterizedConstructor() {
        Admin admin = new Admin("admin@example.com", "password123", "ADMIN", "John", "Doe");
        assertNotNull(admin);
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("password123", admin.getPassword());
        assertEquals("ADMIN", admin.getRoleAdmin());
        assertEquals("John", admin.getNom());
        assertEquals("Doe", admin.getPrenom());
    }

    @Test
    void testSettersAndGetters() {
        Admin admin = new Admin();
        admin.setEmail("newadmin@example.com");
        admin.setPassword("newpass");
        admin.setRoleAdmin("SUPER_ADMIN");
        admin.setNom("Jane");
        admin.setPrenom("Smith");

        assertEquals("newadmin@example.com", admin.getEmail());
        assertEquals("newpass", admin.getPassword());
        assertEquals("SUPER_ADMIN", admin.getRoleAdmin());
        assertEquals("Jane", admin.getNom());
        assertEquals("Smith", admin.getPrenom());
    }

    @Test
    void testToString() {
        Admin admin = new Admin("admin@example.com", "password123", "ADMIN", "John", "Doe");
        String expectedToString = "Admin [role=ADMIN, nom=John, prenom=Doe]";
        assertEquals(expectedToString, admin.toString());
    }
}