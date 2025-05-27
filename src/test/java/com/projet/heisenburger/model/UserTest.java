package com.projet.heisenburger.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserCreation() {
        String email = "test@example.com";
        String password = "password123";
        
        // Using an anonymous inner class to instantiate the abstract User class for testing
        User user = new User(email, password) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testSetters() {
        User user = new User("initial@example.com", "initialPass") {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        int newId = 1;
        String newEmail = "new@example.com";
        String newPassword = "newPassword";

        user.setId(newId);
        user.setEmail(newEmail);
        user.setPassword(newPassword);

        assertEquals(newId, user.getId());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newPassword, user.getPassword());
    }
}