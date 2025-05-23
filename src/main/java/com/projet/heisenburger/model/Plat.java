package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produit") // Nom de la table pour les plats
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_produit;

    private String nom;
    private String description;
    private double prix;
    private boolean image_url;
    private String disponible; // Nom de champ cohérent avec les conventions Java (camelCase)

    @ManyToOne(fetch = FetchType.LAZY) // LAZY est souvent une bonne pratique pour les relations ManyToOne
    @JoinColumn(name = "id_restaurant") // Nom de la colonne clé étrangère dans la table 'plats'
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private Categorie categorie;

    

    public Plat() {
    }

    public int getId_produiot() {
        return id_produit;
    }

    public void setId_produiot(int id_produit) {
        this.id_produit = id_produit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isImage_url() {
        return image_url;
    }

    public void setImage_url(boolean image_url) {
        this.image_url = image_url;
    }

    public String getDisponible() {
        return disponible;
    }

    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Plat{" +
                "id_produiot=" + id_produit +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", image_url=" + image_url +
                ", disponible='" + disponible + '\'' +
                ", restaurant=" + restaurant +
                ", categorie=" + categorie +
                '}';
    }
}       