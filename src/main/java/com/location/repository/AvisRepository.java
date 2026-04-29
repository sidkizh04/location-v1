package com.location.repository;

import com.location.model.Avis;
import com.location.model.Utilisateur;
import com.location.model.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvisRepository extends JpaRepository<Avis, Long> {

    // Tous les avis approuvés d'une voiture (pour la page détail)
    List<Avis> findByVoitureAndStatutOrderByDateAvisDesc(Voiture voiture, String statut);

    // Tous les avis en attente (pour l'admin)
    List<Avis> findByStatutOrderByDateAvisDesc(String statut);

    // Vérifier si un utilisateur a déjà laissé un avis pour une réservation
    Optional<Avis> findByReservationId(Long reservationId);

    // Tous les avis d'une voiture (admin - toutes statuts)
    List<Avis> findByVoitureOrderByDateAvisDesc(Voiture voiture);

    // Moyenne des notes approuvées d'une voiture
    @Query("SELECT AVG(a.note) FROM Avis a WHERE a.voiture = :voiture AND a.statut = 'APPROUVE'")
    Double findMoyenneNoteByVoiture(@Param("voiture") Voiture voiture);

    // Nombre d'avis approuvés d'une voiture
    @Query("SELECT COUNT(a) FROM Avis a WHERE a.voiture = :voiture AND a.statut = 'APPROUVE'")
    Long countAvisApprouvesByVoiture(@Param("voiture") Voiture voiture);

    // Tous les avis (admin - stats globales)
    @Query("SELECT COUNT(a) FROM Avis a WHERE a.statut = :statut")
    Long countByStatut(@Param("statut") String statut);
}