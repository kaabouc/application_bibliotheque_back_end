package com.Biblio.cours.services;



import com.Biblio.cours.dao.DocumentDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Document;

import com.Biblio.cours.entities.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Autowired
    private DocumentDAO documentDao;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Document saveDocument(Document document) {


                // Set the file path to the document entity

                document.setDislike(0);
                document.setLikes(0);



        // Save the document entity in the database
        return documentDao.save(document);
    }
    @Override
    public Document UpdateDocument(Document document) {






        // Save the document entity in the database
        return documentDao.save(document);
    }

    @Override
    public List<Document> getAllDocuments() {
        List<Document> documents = documentDao.findAll();
        System.out.println("Documents retrieved from the database: " + documents);
        return documents;
    }
    @Override
    public List<Document> getDocumentsByUserId(Long userId) {
        Utilisateur user = utilisateurDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentDao.findDocumentByUtilisateur(user);
    }


    @Override
    public Optional<Document> getDocumentById(Long id) {
        return documentDao.findById(id);
    }

    @Override
    public void deleteDocument(Long id) {
        Optional<Document> document = documentDao.findById(id);
        if (document.isPresent()) {
            // Delete the file from the directory
            try {
                Files.deleteIfExists(Paths.get(document.get().getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            documentDao.deleteById(id);
        }
    }

    @Override
    public List<Document> searchDocuments(String titre, String description, String filier, String niveaux, Long bibliothequeId, Long typeId) {
        String query = "SELECT d FROM Document d WHERE 1=1";

        if (titre != null) query += " AND d.titre LIKE :titre";
        if (description != null) query += " AND d.description LIKE :description";
        if (filier != null) query += " AND d.filier = :filier";
        if (niveaux != null) query += " AND d.niveaux = :niveaux";
        if (bibliothequeId != null) query += " AND d.bibliotheque.id = :bibliothequeId";
        if (typeId != null) query += " AND d.type.id = :typeId";

        TypedQuery<Document> typedQuery = entityManager.createQuery(query, Document.class);

        if (titre != null) typedQuery.setParameter("titre", "%" + titre + "%");
        if (description != null) typedQuery.setParameter("description", "%" + description + "%");
        if (filier != null) typedQuery.setParameter("filier", filier);
        if (niveaux != null) typedQuery.setParameter("niveaux", niveaux);
        if (bibliothequeId != null) typedQuery.setParameter("bibliothequeId", bibliothequeId);
        if (typeId != null) typedQuery.setParameter("typeId", typeId);

        return typedQuery.getResultList();
    }
}

