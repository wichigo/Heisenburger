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

    @Bean
    @Transactional // Important pour les opérations qui modifient plusieurs entités liées (Restaurant et Plats)
    CommandLineRunner initData(UserRepository userRepository, PlatRepository platRepository) { // Injection de PlatRepository
        return args -> {
            // Création d'un Admin
            Admin admin = new Admin("admin", "admin123");
            userRepository.save(admin);
            System.out.println("Admin créé: admin/admin123");

            // Création d'un Client
            Client client = new Client("client1", "pass", "77001122");
            userRepository.save(client);
            System.out.println("Client créé: client1/pass, Tel: " + client.getTelephone());

            // Création d'un Restaurant
			Restaurant resto = new Restaurant("resto1", "restopass", "Chez Doudou", "Dakar Plateau", "08h - 23h", "logo.png", "OUVERT");
            // On sauvegarde d'abord le restaurant pour qu'il ait un ID généré par la base de données.
            // Cet ID est nécessaire pour établir la relation avec les plats.
            Restaurant savedResto = userRepository.save(resto); // [cite: 77]
            System.out.println("Restaurant créé: " + savedResto.getUsername() + ", Nom: " + savedResto.getNom() + " avec ID: " + savedResto.getId());

            // Création des plats pour 'savedResto'
            Plat p1 = new Plat();
            p1.setNom("Thieboudienne");
            p1.setDescription("Riz au poisson sénégalais");
            p1.setPrix(3500); // Prix en CFA ou unité monétaire locale
            p1.setDisponible(true);
            p1.setImageUrl("thieb.jpg"); // Chemin relatif ou URL complète
            p1.setRestaurant(savedResto); // Associer le plat au restaurant sauvegardé [cite: 78]

            Plat p2 = new Plat();
            p2.setNom("Yassa Poulet");
            p2.setDescription("Poulet mariné au citron et oignons");
            p2.setPrix(3000);
            p2.setDisponible(true);
            p2.setImageUrl("yassa.jpg");
            p2.setRestaurant(savedResto); // [cite: 78]

            Plat p3 = new Plat();
            p3.setNom("Mafé");
            p3.setDescription("Ragoût de viande à la sauce arachide");
            p3.setPrix(2800);
            p3.setDisponible(false); // Plat non disponible [cite: 79]
            p3.setImageUrl("mafe.jpg");
            p3.setRestaurant(savedResto); // [cite: 79]

            // Sauvegarder tous les plats
            // platRepository.saveAll(List.of(p1, p2, p3));
            // Ou, si on utilise la cascade et la gestion bidirectionnelle :
            savedResto.addPlat(p1);
            savedResto.addPlat(p2);
            savedResto.addPlat(p3);
            userRepository.save(savedResto); // Sauvegarder le restaurant mettra à jour les plats grâce à CascadeType.ALL

            System.out.println("Plats ajoutés pour le restaurant " + savedResto.getNom() + ":");
            savedResto.getPlats().forEach(plat -> System.out.println("- " + plat.getNom() + (plat.isDisponible() ? "" : " (Indisponible)")));
        };
    }
}