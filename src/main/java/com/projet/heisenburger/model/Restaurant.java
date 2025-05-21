package com.projet.heisenburger.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("RESTAURANT")
public class Restaurant extends User {

    private String nom;
    private String adresse;
    private String horaires;
    @Column(name = "logo_url")
    private String logo;
    private String statut;

    // mappedBy="restaurant" indique que l'entité Plat gère la relation (via son champ 'restaurant')
    // CascadeType.ALL: les opérations (persist, merge, remove, refresh, detach) sur Restaurant sont propagées à Plats.
    // orphanRemoval=true: si un Plat est retiré de la liste 'plats' du Restaurant, il sera supprimé de la BDD.
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plat> plats = new ArrayList<>(); // Initialisation de la liste

    public Restaurant() {
        super();
    }

    public Restaurant(String email, String password, String nom, String adresse, String horaires, String logo, String statut) {
        super(email, password);
        this.nom = nom;
        this.adresse = adresse;
        this.horaires = horaires;
        this.logo = logo;
        this.statut = statut;
    } //

    // Getters et Setters pour les attributs spécifiques à Restaurant
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getHoraires() {
        return horaires;
    }

    public void setHoraires(String horaires) {
        this.horaires = horaires;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Getter et Setter pour la liste des plats
    public List<Plat> getPlats() {
        return plats;
    }

    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }

    // Méthodes utilitaires pour gérer la relation bidirectionnelle (bonne pratique)
    public void addPlat(Plat plat) {
        plats.add(plat);
        plat.setRestaurant(this);
    }

    public void removePlat(Plat plat) {
        plats.remove(plat);
        plat.setRestaurant(null);
    }
}