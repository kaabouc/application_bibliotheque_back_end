package com.Biblio.cours.services;



import com.Biblio.cours.dao.DocumentDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Document;

import com.Biblio.cours.entities.Utilisateur;
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

    @Override
    public Document saveDocument(Document document, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                // Create a unique filename
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                String filePath = uploadDirectory + File.separator + fileName; // Use File.separator for cross-platform compatibility

                // Ensure the upload directory exists
                Files.createDirectories(Paths.get(uploadDirectory));

                // Save the file to the specified directory
                file.transferTo(new File(filePath));

                // Set the file path to the document entity
                document.setFilePath(filePath);
                document.setDislike(0);
                document.setLikes(0);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to store the file.");
            }
        }

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
}

