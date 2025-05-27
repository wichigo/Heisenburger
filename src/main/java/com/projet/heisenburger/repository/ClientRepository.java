package com.projet.heisenburger.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.heisenburger.model.Categorie;

public interface ClientRepository extends JpaRepository<Categorie, Integer> {
    
    
}
