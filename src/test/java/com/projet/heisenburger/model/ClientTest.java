package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    @Test
    void testDefaultConstructor() {
        Client client = new Client();
        assertNotNull(client);
        assertNull(client.getEmail());
        assertNull(client.getPassword());
        assertNull(client.getNom());
        assertNull(client.getPrenom());
        assertNull(client.getAdresse());
        assertNull(client.getVille());
        assertNull(client.getTelephone());
        assertNull(client.getDateInscription());
        assertEquals("actif", client.getStatut());
    }

    @Test
    void testParameterizedConstructor() {
        String email = "client@example.com";
        String password = "clientpass";
        String telephone = "0123456789";
        String nom = "ClientNom";
        String prenom = "ClientPrenom";
        String adresse = "123 Main St";
        String ville = "Paris";

        Client client = new Client(email, password, telephone, nom, prenom, adresse, ville);
        assertNotNull(client);
        assertEquals(email, client.getEmail());
        assertEquals(password, client.getPassword());
        assertEquals(telephone, client.getTelephone());
        assertEquals(nom, client.getNom());
        assertEquals(prenom, client.getPrenom());
        assertEquals(adresse, client.getAdresse());
        assertEquals(ville, client.getVille());
        assertNotNull(client.getDateInscription());
        assertEquals("actif", client.getStatut());
    }

    @Test
    void testSettersAndGetters() {
        Client client = new Client();
        client.setEmail("newclient@example.com");
        client.setPassword("newpass");
        client.setNom("NewNom");
        client.setPrenom("NewPrenom");
        client.setAdresse("456 New Ave");
        client.setVille("Lyon");
        client.setTelephone("9876543210");
        LocalDateTime now = LocalDateTime.now();
        client.setDateInscription(now);
        client.setStatut("inactif");

        assertEquals("newclient@example.com", client.getEmail());
        assertEquals("newpass", client.getPassword());
        assertEquals("NewNom", client.getNom());
        assertEquals("NewPrenom", client.getPrenom());
        assertEquals("456 New Ave", client.getAdresse());
        assertEquals("Lyon", client.getVille());
        assertEquals("9876543210", client.getTelephone());
        assertEquals(now, client.getDateInscription());
        assertEquals("inactif", client.getStatut());
    }

    @Test
    void testToString() {
        String email = "client@example.com";
        String password = "clientpass";
        String telephone = "0123456789";
        String nom = "ClientNom";
        String prenom = "ClientPrenom";
        String adresse = "123 Main St";
        String ville = "Paris";

        Client client = new Client(email, password, telephone, nom, prenom, adresse, ville);
        // The toString method includes date_inscription which is LocalDateTime.now(), so we can't hardcode it.
        // We'll check if the string contains the expected parts.
        String clientString = client.toString();
        assertTrue(clientString.contains("nom=" + nom));
        assertTrue(clientString.contains("prenom=" + prenom));
        assertTrue(clientString.contains("adresse=" + adresse));
        assertTrue(clientString.contains("ville=" + ville));
        assertTrue(clientString.contains("telephone=" + telephone));
        assertTrue(clientString.contains("statut=actif"));
        assertTrue(clientString.contains("date_inscription=")); // Check for presence of date
    }
}