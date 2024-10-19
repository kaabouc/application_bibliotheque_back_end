package com.Biblio.cours.service;



import com.Biblio.cours.entities.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IDocumentService {
    // Create or Update Document
    Document saveDocument(Document document, MultipartFile file);

    // Get all Documents
    List<Document> getAllDocuments();

    // Get Document by ID
    Optional<Document> getDocumentById(Long id);

    // Delete Document by ID
    void deleteDocument(Long id);
}

