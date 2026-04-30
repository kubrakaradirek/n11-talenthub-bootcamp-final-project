package com.n11bootcamp.user_service.repository;


import com.n11bootcamp.user_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Her testten önce veritabanını temizleyip taze veri ekleme
        userRepository.deleteAll();
        User user = new User("kubra", "kubra@n11.com", "123456", "Customer");
        userRepository.save(user);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        Boolean exists = userRepository.existsByEmail("kubra@n11.com");
        assertTrue(exists, "Kayıtlı email sorgulandığında true dönmeli");
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        Boolean exists = userRepository.existsByEmail("olmayan@n11.com");
        assertFalse(exists, "Kayıtlı olmayan email sorgulandığında false dönmeli");
    }

    @Test
    void shouldFindUserByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("kubra");
        assertTrue(foundUser.isPresent(), "Kullanıcı adı ile arandığında kullanıcı bulunmalı");
        assertEquals("kubra@n11.com", foundUser.get().getEmail());
    }
}