package com.projet.heisenburger.model;

import jakarta.persistence.*;

/**
 * Représente un plat dans le menu d'un restaurant.
 * Cette entité est mappée à la table "produit" dans la base de données.
 */
@Entity
@Table(name = "produit")
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit") // Mappe à la colonne existante dans la BDD
    private int id;

    private String nom;
    private String description;
    private double prix;
    @Column(name = "image_url") // Mappe à la colonne existante dans la BDD
    private String imageUrl; // Changé en String pour stocker l'URL de l'image

    private String disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant")
    private Restaurant restaurant;

    // La catégorie est maintenant un simple String pour correspondre au formulaire HTML
    private String categorie;

    /**
     * Constructeur par défaut.
     */
    public Plat() {
    }

    /**
     * Retourne l'ID du plat.
     * @return L'ID du plat.
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'ID du plat.
     * @param id Le nouvel ID du plat.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le nom du plat.
     * @return Le nom du plat.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du plat.
     * @param nom Le nouveau nom du plat.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne la description du plat.
     * @return La description du plat.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la description du plat.
     * @param description La nouvelle description du plat.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne le prix du plat.
     * @return Le prix du plat.
     */
    public double getPrix() {
        return prix;
    }

    /**
     * Définit le prix du plat.
     * @param prix Le nouveau prix du plat.
     */
    public void setPrix(double prix) {
        this.prix = prix;
    }

    /**
     * Retourne l'URL de l'image du plat.
     * @return L'URL de l'image du plat.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Définit l'URL de l'image du plat.
     * @param imageUrl La nouvelle URL de l'image du plat.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Retourne le statut de disponibilité du plat.
     * @return Le statut de disponibilité du plat.
     */
    public String getDisponible() {
        return disponible;
    }

    /**
     * Définit le statut de disponibilité du plat.
     * @param disponible Le nouveau statut de disponibilité du plat.
     */
    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }

    /**
     * Retourne le restaurant auquel ce plat appartient.
     * @return Le restaurant.
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Définit le restaurant auquel ce plat appartient.
     * @param restaurant Le nouveau restaurant.
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Retourne la catégorie du plat.
     * @return La catégorie du plat.
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * Définit la catégorie du plat.
     * @param categorie La nouvelle catégorie du plat.
     */
    public void setCategorie(String categorie) {
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
                ", restaurant=" + restaurant +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}