package com.Biblio.cours.services;

import com.Biblio.cours.dao.CommentaireDAO;
import com.Biblio.cours.entities.Commentaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentaireServiceImpl implements ICommentaireService {

    @Autowired
    private CommentaireDAO commentaireRepository;

    @Override
    public Commentaire saveCommentaire(Commentaire commentaire) {
        return commentaireRepository.save(commentaire);
    }

    @Override
    public Commentaire getCommentaireById(Long id) {
        Optional<Commentaire> optionalCommentaire = commentaireRepository.findById(id);
        return optionalCommentaire.orElse(null);
    }

    @Override
    public List<Commentaire> getAllCommentaires() {
        return commentaireRepository.findAll();
    }

    @Override
    public void deleteCommentaire(Long id) {
        commentaireRepository.deleteById(id);
    }

    @Override
    public Commentaire updateCommentaire(Long id, Commentaire updatedCommentaire) {
        return commentaireRepository.findById(id).map(commentaire -> {
            commentaire.setMessage(updatedCommentaire.getMessage());
            commentaire.setDocument(updatedCommentaire.getDocument());
            commentaire.setUtilisateur(updatedCommentaire.getUtilisateur());
            return commentaireRepository.save(commentaire);
        }).orElse(null);
    }

    @Override
    public Optional<Commentaire> findById(Long id) {
        return commentaireRepository.findById(id);
    }

    @Override
    public List<Commentaire> getCommentairesByDocumentId(Long documentId) {
        return commentaireRepository.findByDocumentId(documentId);
    }

    @Override
    public List<Commentaire> getCommentairesByUserId(Long userId) {
        return commentaireRepository.findByUtilisateurId(userId);
    }
}