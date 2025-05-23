package com.projet.heisenburger.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

import java.math.BigDecimal;


@Entity
@Table(name = "commande")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_commande;

    private LocalDateTime date_commande;
    private String statut;
    private BigDecimal montant_total;
    private String adresse_livraison;
    private String instructions;
    private int numero_transaction;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY est souvent une bonne pratique pour les relations ManyToOne
    @JoinColumn(name = "id_client", referencedColumnName = "id_client")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "id_restaurant", referencedColumnName = "id_restaurant")
    private Restaurant restaurant;

    public Commande() {
    }

    public Long getIdCommande() {
        return id_commande;
    }

    public void setIdCommande(Long id_commande) {
        this.id_commande = id_commande;
    }

    public LocalDateTime getDateCommande() {
        return date_commande;
    }

    public void setDateCommande(LocalDateTime date_commande) {
        this.date_commande = date_commande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantTotal() {
        return montant_total;
    }

    public void setMontantTotal(BigDecimal montant_total) {
        this.montant_total = montant_total;
    }

    public String getAdresseLivraison() {
        return adresse_livraison;
    }

    public void setAdresseLivraison(String adresse_livraison) {
        this.adresse_livraison = adresse_livraison;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getNumeroTransaction() {
        return numero_transaction;
    }

    public void setNumeroTransaction(int numero_transaction) {
        this.numero_transaction = numero_transaction;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

   @Override
    public String toString() {
        return "Commande [idCommande=" + id_commande +
               ", dateCommande=" + date_commande +
               ", statut=" + statut +
               ", montantTotal=" + montant_total +
               ", adresseLivraison=" + adresse_livraison +
               // Adaptez en fonction des ID réels de Client et Restaurant
               ", client=" + (client != null ? client.getId() : "null") + 
               ", restaurant=" + (restaurant != null ? restaurant.getId() : "null") +
               "]";
    }

}