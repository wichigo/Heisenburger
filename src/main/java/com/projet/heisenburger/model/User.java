package com.projet.heisenburger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "login") // Conserve le nom de table
@Inheritance(strategy = InheritanceType.JOINED) // Stratégie d'héritage
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING) // Colonne pour distinguer les types d'utilisateurs
public abstract class User { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_login")
    protected int id; 

    protected String email; 
    protected String password;

    // Constructeur par défaut (requis par JPA)
    public User() {
    }

    // Constructeur pour les sous-classes
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

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
               ", email='" + email + "'" +
               ", userType='" + (this.getClass().isAnnotationPresent(DiscriminatorValue.class) ? this.getClass().getAnnotation(DiscriminatorValue.class).value() : "N/A") + "'" +
               '}';
    }
}