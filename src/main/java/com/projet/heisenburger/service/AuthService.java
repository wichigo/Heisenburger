package com.projet.heisenburger.service;

import com.projet.heisenburger.model.User;
import com.projet.heisenburger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                             .map(user -> user.getPassword().equals(password))
                             .orElse(false);
    }

    public User findUser(String username) {
        return userRepository.findByUsername(username)
                             .orElse(null);
    }
}