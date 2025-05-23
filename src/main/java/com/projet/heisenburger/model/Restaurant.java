package com.projet.heisenburger.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@DiscriminatorValue("RESTAURANT")

public class Restaurant extends User {

    @Column(name = "id_restaurant", insertable=false, updatable=false)
    private int idRestaurant;
    private String nom;
    private String description;
    private String adresse;
    private int telephone;
    @Column(name = "logo_url")
    private String logoUrl;
    private String statut;
    private LocalDateTime date_inscription;
    private String horaires;



    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER) // fetch = FetchType.EAGER peut simplifier pour l'instant, sinon LAZY et gérer la session/transaction
    private List<Plat> plats = new ArrayList<>(); // Décommenté et initialisé

    // mappedBy="restaurant" indique que l'entité Plat gère la relation (via son champ 'restaurant')
    // CascadeType.ALL: les opérations (persist, merge, remove, refresh, detach) sur Restaurant sont propagées à Plats.
    // orphanRemoval=true: si un Plat est retiré de la liste 'plats' du Restaurant, il sera supprimé de la BDD.

    public Restaurant() {
        super();
    }

    public Restaurant(String email, String password, String nom, String description, String adresse, int telephone, String logoUrl, String statut, LocalDateTime date_inscription, String horaires) {
        super(email, password);
        this.nom = nom;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.logoUrl = logoUrl;
        this.statut = statut;
        this.date_inscription = date_inscription;
        this.horaires = horaires;
    }

    public int getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(int idRestaurant) {
        this.idRestaurant = idRestaurant;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateInscription() {
        return date_inscription;
    }

    public void setDateInscription(LocalDateTime date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getHoraires() {
        return horaires;
    }

    public void setHoraires(String horaires) {
        this.horaires = horaires;
    }

    @Override
    public String toString() {
        return "Restaurant [id_restaurant=" + idRestaurant + ", nom=" + nom + ", description=" + description
                + ", adresse=" + adresse + ", telephone=" + telephone + ", logoUrl=" + logoUrl + ", statut=" + statut
                + ", date_inscription=" + date_inscription + ", horaires=" + horaires + "]";
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