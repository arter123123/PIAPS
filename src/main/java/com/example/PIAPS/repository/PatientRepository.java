package com.example.PIAPS.repository;

import com.example.PIAPS.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Методы могут быть расширены при необходимости (например, поиск по имени или дате рождения).
}
