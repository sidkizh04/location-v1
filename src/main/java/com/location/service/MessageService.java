package com.location.service;

import com.location.model.Message;
import com.location.model.Utilisateur;
import com.location.repository.MessageRepository;
import com.location.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;

    public MessageService(MessageRepository messageRepository,
                          UtilisateurRepository utilisateurRepository) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Message envoyerMessage(String contenu, Utilisateur expediteur,
                                  Utilisateur destinataire) {
        Message msg = new Message();
        msg.setContenu(contenu);
        msg.setExpediteur(expediteur);
        msg.setDestinataire(destinataire);
        msg.setDateEnvoi(LocalDateTime.now());
        return messageRepository.save(msg);
    }

    public List<Message> getConversation(Utilisateur u1, Utilisateur u2) {
        return messageRepository.findConversation(u1, u2);
    }

    public long countNonLus(Utilisateur destinataire) {
        return messageRepository.findByDestinataireAndLuFalse(destinataire).size();
    }

    public void marquerCommeLus(Utilisateur expediteur, Utilisateur destinataire) {
        List<Message> msgs = messageRepository.findConversation(expediteur, destinataire);
        msgs.stream()
                .filter(m -> m.getDestinataire().equals(destinataire) && !m.isLu())
                .forEach(m -> {
                    m.setLu(true);
                    messageRepository.save(m);
                });
    }
}