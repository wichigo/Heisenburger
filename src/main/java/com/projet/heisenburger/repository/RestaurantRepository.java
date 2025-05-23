package com.projet.heisenburger.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projet.heisenburger.model.Restaurant;
import java.util.Optional;



public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> { 

    //liste restaurant si statut = "actif"
    List<Restaurant> findAllByStatut(String statut);

    List<Restaurant> findByAdresseContaining(String searchTerm);

    Optional<Restaurant> findByIdRestaurant(int idRestaurant);
}