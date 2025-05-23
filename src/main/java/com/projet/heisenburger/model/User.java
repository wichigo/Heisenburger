package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "login") // Conserve le nom de table
@Inheritance(strategy = InheritanceType.JOINED) // Stratégie d'héritage
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING) // Colonne pour distinguer les types d'utilisateurs
public abstract class User { // La classe devient abstraite

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_login")
    protected int id; // Passage en protected pour l'accès par les sous-classes

    protected String email; // Passage en protected
    protected String password; // Passage en protected

    // Constructeur par défaut (requis par JPA)
    public User() {
    }

    // Constructeur pour les sous-classes
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", email='" + email + "" +
               // Ne pas inclure le mot de passe
               ", userType='" + this.getClass().getAnnotation(DiscriminatorValue.class).value() + "" +
               '}';
    }
}