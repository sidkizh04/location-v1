package com.location.repository;

import com.location.model.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoitureRepository extends JpaRepository<Voiture, Long> {
    List<Voiture> findByDisponibleTrue();
}