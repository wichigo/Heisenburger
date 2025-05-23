package com.projet.heisenburger.model;

import java.time.LocalDateTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "client")
@DiscriminatorValue("CLIENT") // Valeur pour la colonne role
public class Client extends User {

    private Long id_client;
    private String nom;
    private String prenom;
    private String adresse;
    private String ville;
    private String telephone;
    private LocalDateTime date_inscription;
    private String statut;


    public Client() {
        super();
    }

    public Client(String email, String password, Long id_client, String telephone, String nom, String prenom, String adresse, String ville, String statut) {
        super(email, password);
        this.id_client = id_client;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.ville = ville;
        this.telephone = telephone;
        this.date_inscription = LocalDateTime.now();
        this.statut = statut;
    }

    public Long getIdClient() {
        return id_client;
    }

    public void setIdClient(Long id_client) {
        this.id_client = id_client;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDateTime getDateInscription() {
        return date_inscription;
    }

    public void setDateInscription(LocalDateTime date_inscription) {
        this.date_inscription = date_inscription;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Client [id_client=" + id_client + ", nom=" + nom + ", prenom=" + prenom + ", adresse=" + adresse
                + ", ville=" + ville + ", telephone=" + telephone + ", date_inscription=" + date_inscription
                + ", statut=" + statut + "]";
    }


}