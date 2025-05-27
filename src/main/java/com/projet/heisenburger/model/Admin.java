package com.projet.heisenburger.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateur")
@DiscriminatorValue("ADMIN") // Valeur pour la colonne role
public class Admin extends User {

    private String role_admin;
    private String nom;
    private String prenom;

    public Admin() {
        super();
        
    }

    public Admin(String email, String password, String role_admin, String nom, String prenom) {
        super(email, password);
        this.role_admin = role_admin;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    public String getRoleAdmin() {
        return role_admin;
    }

    public void setRoleAdmin(String role) {
        this.role_admin = role;
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

    @Override
    public String toString() {
        return "Admin [role=" + role_admin + ", nom=" + nom + ", prenom=" + prenom + "]";
    }  
   
}