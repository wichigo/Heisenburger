package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plats") // Nom de la table pour les plats
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private double prix;
    private boolean disponible;
    private String imageUrl; // Nom de champ cohérent avec les conventions Java (camelCase)

    @ManyToOne(fetch = FetchType.LAZY) // LAZY est souvent une bonne pratique pour les relations ManyToOne
    @JoinColumn(name = "restaurant_id") // Nom de la colonne clé étrangère dans la table 'plats'
    private Restaurant restaurant;

    // Constructeur par défaut
    public Plat() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public double getPrix() {
        return prix;
    }

    public boolean isDisponible() { // Convention pour les booléens: isDisponible()
        return disponible;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Plat{" +
               "id=" + id +
               ", nom='" + nom + '\'' +
               ", prix=" + prix +
               ", disponible=" + disponible +
               (restaurant != null ? ", restaurant_id=" + restaurant.getId() : "") +
               '}';
    }
}