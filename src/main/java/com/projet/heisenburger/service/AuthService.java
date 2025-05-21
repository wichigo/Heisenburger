package com.projet.heisenburger.service;

import com.projet.heisenburger.model.User;
import com.projet.heisenburger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                             .map(user -> user.getPassword().equals(password))
                             .orElse(false);
    }

    public User findUser(String email) {
        return userRepository.findByEmail(email)
                             .orElse(null);
    }
}