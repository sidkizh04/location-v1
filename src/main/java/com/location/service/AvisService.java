package com.location.service;

import com.location.model.Avis;
import com.location.model.Reservation;
import com.location.model.Utilisateur;
import com.location.model.Voiture;
import com.location.repository.AvisRepository;
import com.location.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AvisService {

    private final AvisRepository avisRepository;
    private final ReservationRepository reservationRepository;

    public AvisService(AvisRepository avisRepository,
                       ReservationRepository reservationRepository) {
        this.avisRepository = avisRepository;
        this.reservationRepository = reservationRepository;
    }

    // ─── CLIENT ────────────────────────────────────────────────────────────────

    /**
     * Laisser un avis — vérifie que :
     * 1. La réservation appartient bien à cet utilisateur
     * 2. La réservation est CONFIRMEE (ou TERMINEE)
     * 3. L'utilisateur n'a pas déjà laissé un avis pour cette réservation
     */
    public boolean laisserAvis(Long reservationId, Integer note,
                               String commentaire, Utilisateur utilisateur) {

        Optional<Reservation> optRes = reservationRepository.findById(reservationId);
        if (optRes.isEmpty()) return false;

        Reservation reservation = optRes.get();

        // Vérifie que la réservation appartient à l'utilisateur
        if (!reservation.getUtilisateur().getId().equals(utilisateur.getId())) {
            return false;
        }

        // Vérifie que la réservation est bien CONFIRMEE ou TERMINEE
        String statut = reservation.getStatut();
        if (!"CONFIRMEE".equals(statut) && !"TERMINEE".equals(statut)) {
            return false;
        }

        // Vérifie qu'il n'y a pas déjà un avis pour cette réservation
        if (avisRepository.findByReservationId(reservationId).isPresent()) {
            return false;
        }

        // Vérifie que la note est entre 1 et 5
        if (note == null || note < 1 || note > 5) return false;

        Avis avis = new Avis();
        avis.setNote(note);
        avis.setCommentaire(commentaire);
        avis.setStatut("EN_ATTENTE");
        avis.setVoiture(reservation.getVoiture());
        avis.setUtilisateur(utilisateur);
        avis.setReservation(reservation);

        avisRepository.save(avis);
        return true;
    }

    /**
     * Vérifie si une réservation a déjà un avis
     */
    public boolean aDejaUnAvis(Long reservationId) {
        return avisRepository.findByReservationId(reservationId).isPresent();
    }

    // ─── PAGE DÉTAIL VOITURE ────────────────────────────────────────────────────

    /**
     * Avis approuvés d'une voiture (affichage public)
     */
    public List<Avis> getAvisApprouves(Voiture voiture) {
        return avisRepository.findByVoitureAndStatutOrderByDateAvisDesc(voiture, "APPROUVE");
    }

    /**
     * Moyenne des notes d'une voiture (avis approuvés uniquement)
     */
    public Double getMoyenneNote(Voiture voiture) {
        Double moyenne = avisRepository.findMoyenneNoteByVoiture(voiture);
        return moyenne != null ? Math.round(moyenne * 10.0) / 10.0 : null;
    }

    /**
     * Nombre d'avis approuvés d'une voiture
     */
    public Long getNombreAvis(Voiture voiture) {
        return avisRepository.countAvisApprouvesByVoiture(voiture);
    }

    // ─── ADMIN ──────────────────────────────────────────────────────────────────

    /**
     * Tous les avis en attente de modération
     */
    public List<Avis> getAvisEnAttente() {
        return avisRepository.findByStatutOrderByDateAvisDesc("EN_ATTENTE");
    }

    /**
     * Tous les avis (toutes statuts)
     */
    public List<Avis> getTousLesAvis() {
        return avisRepository.findAll();
    }

    /**
     * Approuver un avis
     */
    public void approuver(Long avisId) {
        avisRepository.findById(avisId).ifPresent(avis -> {
            avis.setStatut("APPROUVE");
            avisRepository.save(avis);
        });
    }

    /**
     * Rejeter un avis
     */
    public void rejeter(Long avisId) {
        avisRepository.findById(avisId).ifPresent(avis -> {
            avis.setStatut("REJETE");
            avisRepository.save(avis);
        });
    }

    /**
     * Supprimer un avis
     */
    public void supprimer(Long avisId) {
        avisRepository.deleteById(avisId);
    }

    /**
     * Statistiques globales pour le dashboard admin
     * Retourne : total, enAttente, approuves, rejetes
     */
    public Map<String, Long> getStatistiquesGlobales() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", avisRepository.count());
        stats.put("enAttente", avisRepository.countByStatut("EN_ATTENTE"));
        stats.put("approuves", avisRepository.countByStatut("APPROUVE"));
        stats.put("rejetes", avisRepository.countByStatut("REJETE"));
        return stats;
    }

    /**
     * Statistiques par voiture (pour admin)
     */
    public Map<String, Object> getStatistiquesVoiture(Voiture voiture) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("moyenne", getMoyenneNote(voiture));
        stats.put("nombreAvis", getNombreAvis(voiture));
        stats.put("tousLesAvis", avisRepository.findByVoitureOrderByDateAvisDesc(voiture));
        return stats;
    }
}