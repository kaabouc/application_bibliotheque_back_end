package com.Biblio.cours.services;



import com.Biblio.cours.dao.DocumentDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Document;

import com.Biblio.cours.entities.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public List<Document> searchDocuments(String titre, List<Long> bibliotheques, List<Long> types,
                                          List<String> filier, List<String> niveaux) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Document> query = cb.createQuery(Document.class);
        Root<Document> document = query.from(Document.class);

        List<Predicate> predicates = new ArrayList<>();

        if (titre != null && !titre.isEmpty()) {
            predicates.add(cb.like(cb.lower(document.get("titre")), "%" + titre.toLowerCase() + "%"));
        }
        if (bibliotheques != null && !bibliotheques.isEmpty()) {
            predicates.add(document.get("bibliotheque").get("id").in(bibliotheques));
        }
        if (types != null && !types.isEmpty()) {
            predicates.add(document.get("type").get("id").in(types));
        }
        if (filier != null && !filier.isEmpty()) {
            predicates.add(document.get("filier").in(filier));
        }
        if (niveaux != null && !niveaux.isEmpty()) {
            predicates.add(document.get("niveaux").in(niveaux));
        }

        query.select(document).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}

