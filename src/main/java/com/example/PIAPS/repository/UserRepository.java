package com.example.PIAPS.repository;
import com.example.PIAPS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Методы могут быть расширены при необходимости (например, поиск по имени пользователя или email).
    Optional<User> findByEmail(String email);
    Optional<User> findByRegistrationToken(String token);
}
