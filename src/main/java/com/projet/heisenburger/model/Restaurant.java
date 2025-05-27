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
    private String telephone;
    @Column(name = "logo_url")
    private String logoUrl;
    private String statut;
    private LocalDateTime date_inscription;
    private String horaires;



    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    private List<Plat> plats = new ArrayList<>(); 


    public Restaurant() {
        super();
    }

    public Restaurant(String email, String password, String nom, String description, String adresse, String telephone, String logoUrl, String statut, LocalDateTime date_inscription, String horaires) {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
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



    public List<Plat> getPlats() {
        return plats;
    }

    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }

}