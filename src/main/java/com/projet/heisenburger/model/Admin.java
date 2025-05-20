package com.projet.heisenburger.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN") // Valeur pour la colonne user_type
public class Admin extends User {

    // Pas d'attribut supplémentaire pour Admin dans cet exemple [cite: 32]

    public Admin() {
        super();
    }

    public Admin(String username, String password) {
        super(username, password);
    }
}