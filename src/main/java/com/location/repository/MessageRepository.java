package com.location.repository;

import com.location.model.Message;
import com.location.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Conversation entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
            "(m.expediteur = :u1 AND m.destinataire = :u2) OR " +
            "(m.expediteur = :u2 AND m.destinataire = :u1) " +
            "ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(@Param("u1") Utilisateur u1,
                                   @Param("u2") Utilisateur u2);

    // Messages non lus pour un utilisateur
    List<Message> findByDestinataireAndLuFalse(Utilisateur destinataire);

    // Tous les messages reçus par un utilisateur
    List<Message> findByDestinataireOrderByDateEnvoiDesc(Utilisateur destinataire);
}