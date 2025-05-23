package com.projet.heisenburger.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.heisenburger.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    
    
}
