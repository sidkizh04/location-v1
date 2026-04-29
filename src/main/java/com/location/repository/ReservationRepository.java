package com.location.repository;

import com.location.model.Reservation;
import com.location.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateur(Utilisateur utilisateur);
    List<Reservation> findByStatut(String statut);
}