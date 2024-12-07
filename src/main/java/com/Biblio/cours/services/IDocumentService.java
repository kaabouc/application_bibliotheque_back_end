package com.Biblio.cours.services;

import com.Biblio.cours.entities.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface IDocumentService {
    // Existing methods
    Document saveDocument(Document document);
    Document UpdateDocument(Document document);
    List<Document> getAllDocuments();
    Optional<Document> getDocumentById(Long id);
    void deleteDocument(Long id);
    List<Document> getDocumentsByUserId(Long userId);

    // Enhanced search method with additional parameters
    Page<Document> searchDocuments(
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
            Pageable pageable);

    // Additional utility methods
    List<String> getAllFiliers();
    List<String> getAllNiveaux();
    List<String> getPopularTags();

}