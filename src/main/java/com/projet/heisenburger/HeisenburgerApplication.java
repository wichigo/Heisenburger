package com.projet.heisenburger;

import com.projet.heisenburger.model.Admin;
import com.projet.heisenburger.model.Client;
import com.projet.heisenburger.model.Plat; // Importer Plat
import com.projet.heisenburger.model.Restaurant;
import com.projet.heisenburger.repository.PlatRepository; // Importer PlatRepository
import com.projet.heisenburger.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional; // Pour la gestion transactionnelle


@SpringBootApplication
public class HeisenburgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeisenburgerApplication.class, args);
    }

}