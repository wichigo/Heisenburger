package com.projet.heisenburger.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT") // Valeur pour la colonne user_type
public class Client extends User {

    private String telephone;

    public Client() {
        super();
    }

    public Client(String username, String password, String telephone) {
        super(username, password);
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