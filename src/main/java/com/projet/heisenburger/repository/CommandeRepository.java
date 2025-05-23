package com.projet.heisenburger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet.heisenburger.model.Commande;
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.model.Client;


@Repository
public interface CommandeRepository extends JpaRepository<Commande, Integer> {

        List<Commande> findByRestaurant(Restaurant restaurant);
        List<Commande> findByClient(Client client);
        List<Commande> findAllByStatut(String statut);
        
}
