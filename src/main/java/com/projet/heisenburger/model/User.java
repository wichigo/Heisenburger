package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Conserve le nom de table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Stratégie d'héritage
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // Colonne pour distinguer les types d'utilisateurs
public abstract class User { // La classe devient abstraite

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // Passage en protected pour l'accès par les sous-classes

    protected String username; // Passage en protected
    protected String password; // Passage en protected

    // Constructeur par défaut (requis par JPA)
    public User() {
    }

    // Constructeur pour les sous-classes
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               // Ne pas inclure le mot de passe
               ", userType='" + this.getClass().getAnnotation(DiscriminatorValue.class).value() + '\'' +
               '}';
    }
}