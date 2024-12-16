package com.example.PIAPS.repository;

import com.example.PIAPS.model.ProtocolTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocolTemplateRepository extends JpaRepository<ProtocolTemplate, Long> {
    // Методы могут быть добавлены для поиска по названию или содержимому шаблона.
}

