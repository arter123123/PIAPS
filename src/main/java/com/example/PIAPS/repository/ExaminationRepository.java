package com.example.PIAPS.repository;
import com.example.PIAPS.model.Examination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    // Методы могут быть расширены для поиска по параметрам, пациентам и пользователям.
    // Простой поиск по названию
    Page<Examination> findByTitleContaining(String title, PageRequest pageRequest);

    // Поиск по пациенту
    Page<Examination> findByPatientId(Long patientId, PageRequest pageRequest);
}