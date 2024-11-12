package com.Biblio.cours.dao;



import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentDAO extends JpaRepository<Document, Long> {
    Document findByTitre(String titre);
    List<Document> findDocumentByUtilisateur(Utilisateur user);

}


