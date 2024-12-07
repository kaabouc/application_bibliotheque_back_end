package com.Biblio.cours.services;

import com.Biblio.cours.dao.DocumentDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Autowired
    private DocumentDAO documentDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @Override
    public Document saveDocument(Document document) {
        document.setDislike(0);
        document.setLikes(0);
        return documentDao.save(document);
    }

    @Override
    public Document UpdateDocument(Document document) {
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
            try {
                Files.deleteIfExists(Paths.get(document.get().getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            documentDao.deleteById(id);
        }
    }

    @Override
    public Page<Document> searchDocuments(
            String searchTerm,
            String filier,
            String niveaux,
            Long bibliothequeId,
            Long typeId,
            Integer minLikes,
            Integer maxLikes,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean hasAttachments,
            List<String> tags,
            String sortBy,
            String sortDirection,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Document> query = cb.createQuery(Document.class);
        Root<Document> document = query.from(Document.class);
        List<Predicate> predicates = new ArrayList<>();

        // Search term (titre or description)
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(document.get("titre")), searchPattern),
                    cb.like(cb.lower(document.get("description")), searchPattern)
            ));
        }

        // Basic filters
        if (filier != null) predicates.add(cb.equal(document.get("filier"), filier));
        if (niveaux != null) predicates.add(cb.equal(document.get("niveaux"), niveaux));
        if (bibliothequeId != null) predicates.add(cb.equal(document.get("bibliotheque").get("id"), bibliothequeId));
        if (typeId != null) predicates.add(cb.equal(document.get("type").get("id"), typeId));

        // Likes range
        if (minLikes != null) predicates.add(cb.greaterThanOrEqualTo(document.get("likes"), minLikes));
        if (maxLikes != null) predicates.add(cb.lessThanOrEqualTo(document.get("likes"), maxLikes));

        // Date range
        if (startDate != null) predicates.add(cb.greaterThanOrEqualTo(document.get("createdAt"), startDate));
        if (endDate != null) predicates.add(cb.lessThanOrEqualTo(document.get("createdAt"), endDate));

        // Has attachments
        if (hasAttachments != null) {
            if (hasAttachments) {
                predicates.add(cb.isNotNull(document.get("filePath")));
            } else {
                predicates.add(cb.isNull(document.get("filePath")));
            }
        }

        // Tags
        if (tags != null && !tags.isEmpty()) {
            predicates.add(document.get("tags").in(tags));
        }

        // Apply all predicates
        query.where(predicates.toArray(new Predicate[0]));

        // Sorting
        if (sortBy != null && sortDirection != null) {
            Order order = sortDirection.equalsIgnoreCase("DESC") ?
                    cb.desc(document.get(sortBy)) :
                    cb.asc(document.get(sortBy));
            query.orderBy(order);
        }

        // Execute query with pagination
        List<Document> results = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Document> countRoot = countQuery.from(Document.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<String> getAllFiliers() {
        return entityManager.createQuery(
                "SELECT DISTINCT d.filier FROM Document d WHERE d.filier IS NOT NULL",
                String.class
        ).getResultList();
    }

    @Override
    public List<String> getAllNiveaux() {
        return entityManager.createQuery(
                "SELECT DISTINCT d.niveaux FROM Document d WHERE d.niveaux IS NOT NULL",
                String.class
        ).getResultList();
    }

    @Override
    public List<String> getPopularTags() {
        return entityManager.createQuery(
                        "SELECT t.name, COUNT(d) as count FROM Document d JOIN d.tags t GROUP BY t.name ORDER BY count DESC",
                        String.class
                ).setMaxResults(10)
                .getResultList();
    }
}