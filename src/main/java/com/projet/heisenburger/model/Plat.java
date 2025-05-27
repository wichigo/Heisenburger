package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produit")
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private int id;

    private String nom;
    private String description;
    private double prix;
    @Column(name = "image_url")
    private String imageUrl;

    private String disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant", referencedColumnName = "id_restaurant")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "id_categorie") 
    private Categorie categorie;


    
    public Plat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", imageUrl='" + imageUrl + '\'' +
                ", disponible='" + disponible + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getId() : "null") + // Utilise la PK du restaurant pour l'affichage
                ", categorie=" + (categorie != null ? categorie.getNom() : "null") +
                '}';
    }
}