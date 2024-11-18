package com.Biblio.cours.services;



import com.Biblio.cours.entities.Commentaire;
import java.util.List;
import java.util.Optional;

public interface ICommentaireService {
    Commentaire saveCommentaire(Commentaire commentaire);
    Commentaire getCommentaireById(Long id);
    List<Commentaire> getAllCommentaires();
    void deleteCommentaire(Long id);
    Commentaire updateCommentaire(Long id, Commentaire commentaire);
    Optional<Commentaire> findById(Long id);
    List<Commentaire> getCommentairesByDocumentId(Long documentId);
    List<Commentaire> getCommentairesByUserId(Long userId);
}
