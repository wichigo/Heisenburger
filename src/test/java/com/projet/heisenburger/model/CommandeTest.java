package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CommandeTest {

    @Test
    void testDefaultConstructor() {
        Commande commande = new Commande();
        assertNotNull(commande);
        assertEquals(0, commande.getIdCommande());
        assertNull(commande.getDateCommande());
        assertNull(commande.getStatut());
        assertNull(commande.getMontantTotal());
        assertNull(commande.getAdresseLivraison());
        assertNull(commande.getInstructions());
        assertEquals(0, commande.getNumeroTransaction());
        assertNull(commande.getClient());
        assertNull(commande.getRestaurant());
    }

    @Test
    void testSettersAndGetters() {
        Commande commande = new Commande();
        commande.setIdCommande(1);
        LocalDateTime now = LocalDateTime.now();
        commande.setDateCommande(now);
        commande.setStatut("EN_PREPARATION");
        BigDecimal montant = new BigDecimal("25.50");
        commande.setMontantTotal(montant);
        commande.setAdresseLivraison("123 Test St");
        commande.setInstructions("No onions");
        commande.setNumeroTransaction(12345);

        Client client = new Client();
        client.setId(10);
        commande.setClient(client);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(20);
        commande.setRestaurant(restaurant);

        assertEquals(1, commande.getIdCommande());
        assertEquals(now, commande.getDateCommande());
        assertEquals("EN_PREPARATION", commande.getStatut());
        assertEquals(montant, commande.getMontantTotal());
        assertEquals("123 Test St", commande.getAdresseLivraison());
        assertEquals("No onions", commande.getInstructions());
        assertEquals(12345, commande.getNumeroTransaction());
        assertEquals(client, commande.getClient());
        assertEquals(restaurant, commande.getRestaurant());
    }

    @Test
    void testToString() {
        Commande commande = new Commande();
        commande.setIdCommande(1);
        LocalDateTime now = LocalDateTime.now();
        commande.setDateCommande(now);
        commande.setStatut("EN_PREPARATION");
        BigDecimal montant = new BigDecimal("25.50");
        commande.setMontantTotal(montant);
        commande.setAdresseLivraison("123 Test St");

        Client client = new Client();
        client.setId(10);
        commande.setClient(client);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(20);
        commande.setRestaurant(restaurant);

        String expectedToString = "Commande [idCommande=1, dateCommande=" + now + ", statut=EN_PREPARATION, montantTotal=25.50, adresseLivraison=123 Test St, client=10, restaurant=20]";
        assertEquals(expectedToString, commande.toString());
    }
}