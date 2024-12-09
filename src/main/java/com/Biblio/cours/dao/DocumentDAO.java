package com.Biblio.cours.dao;



import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentDAO extends JpaRepository<Document, Long> {
    Document findByTitre(String titre);
    List<Document> findDocumentByUtilisateur(Utilisateur user);

    @Query("SELECT d FROM Document d " +
            "WHERE (:titre IS NULL OR LOWER(d.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) " +
            "AND (:bibliotheques IS NULL OR d.bibliotheque.id IN :bibliotheques) " +
            "AND (:types IS NULL OR d.type.id IN :types) " +
            "AND (:filier IS NULL OR d.filier IN :filier) " +
            "AND (:niveaux IS NULL OR d.niveaux IN :niveaux)")
    List<Document> searchDocuments(@Param("titre") String titre,
                                   @Param("bibliotheques") List<Long> bibliotheques,
                                   @Param("types") List<Long> types,
                                   @Param("filier") List<String> filier,
                                   @Param("niveaux") List<String> niveaux);

}


