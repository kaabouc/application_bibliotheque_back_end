package com.Biblio.cours.services;



import com.Biblio.cours.entities.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IDocumentService {
    // Create or Update Document

    Document saveDocument(Document document);

     Document UpdateDocument(Document document);
    // Get all Documents
    List<Document> getAllDocuments();

    // Get Document by ID
    Optional<Document> getDocumentById(Long id);

    // Delete Document by ID
    void deleteDocument(Long id);

    public List<Document> getDocumentsByUserId(Long userId);

    List<Document> searchDocuments(String titre, List<Long> bibliotheques, List<Long> types, List<String> filier, List<String> niveaux);

}

