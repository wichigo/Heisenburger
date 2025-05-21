package com.projet.heisenburger.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT") // Valeur pour la colonne role
public class Client extends User {

    private String telephone;

    public Client() {
        super();
    }

    public Client(String email, String password, String telephone) {
        super(email, password);
        this.telephone = telephone;
    }

    // Getters et Setters pour telephone
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}