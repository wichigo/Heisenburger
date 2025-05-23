package com.projet.heisenburger.repository;

import com.projet.heisenburger.model.Plat;
import com.projet.heisenburger.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlatRepository extends JpaRepository<Plat, Integer> {

    // Méthode pour trouver tous les plats appartenant à un restaurant spécifique
    List<Plat> findByRestaurant(Restaurant restaurant);

    List<Plat> findByRestaurantId(Integer restaurantId);

}