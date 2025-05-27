package com.projet.heisenburger.repository;

import com.projet.heisenburger.model.Plat; 
import com.projet.heisenburger.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlatRepository extends JpaRepository<Plat, Integer> {

    List<Plat> findByRestaurant(Restaurant restaurant);

    List<Plat> findByRestaurantId(Integer restaurantId);

}