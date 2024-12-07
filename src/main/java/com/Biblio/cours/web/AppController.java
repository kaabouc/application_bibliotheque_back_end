package com.Biblio.cours.web;

import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.dto.BibliothequeDTO;
import com.Biblio.cours.dto.DocumentResponse;
import com.Biblio.cours.entities.*;
import com.Biblio.cours.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000", "https://e-read-me.onrender.com"})
@RestController
@RequestMapping("/api")
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private IUtilisateurService utilisateurService;

    @Autowired
    private IBibliothequeService bibliothequeService;

    @Autowired
    private IDocumentService documentService;

    @Autowired
    private ICommentaireService commentaireService;

    @Autowired
    private BibliothequeDAO bibliothequeDAO;

    @Autowired
    private TypeDAO typeDAO;

    @Autowired
    private UtilisateurDAO utilisateurDAO;

    @Autowired
    private ITypeService typeService;

    @Value("${file.upload-dir}")
    private String DOCUMENTS_DIR;

    @PostMapping("/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

    @DeleteMapping("/bibliotique/delete/{id}")
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        bibliothequeService.deleteBibliotheque(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/document/search")
    public ResponseEntity<Page<Document>> searchDocuments(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String filier,
            @RequestParam(required = false) String niveaux,
            @RequestParam(required = false) Long bibliothequeId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Integer minLikes,
            @RequestParam(required = false) Integer maxLikes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Boolean hasAttachments,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Document> results = documentService.searchDocuments(
                searchTerm, filier, niveaux, bibliothequeId, typeId,
                minLikes, maxLikes, startDate, endDate,
                hasAttachments, tags, sortBy, sortDirection, pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/bibliotique/all")
    public ResponseEntity<List<BibliothequeDTO>> getAllBibliotheques() {
        List<Bibliotheque> bibliotheques = bibliothequeService.getAllBibliotheques();
        List<BibliothequeDTO> dtos = bibliotheques.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    private BibliothequeDTO convertToDTO(Bibliotheque bibliotheque) {
        BibliothequeDTO dto = new BibliothequeDTO();
        dto.setId(bibliotheque.getId());
        dto.setNom(bibliotheque.getNom());
        dto.setLocation(bibliotheque.getLocation());
        dto.setDocumentsCount(bibliotheque.getDocuments().size());
        return dto;
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<Optional<Utilisateur>> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> user = utilisateurService.getUtilisateurByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/document/like/{id}")
    public ResponseEntity<Document> LikeDocument(@PathVariable("id") Long id) {
        logger.info("Starting like operation for document ID: {}", id);

        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (existingDocument.isEmpty()) {
            logger.warn("Document not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Document document = existingDocument.get();
        document.setLikes(document.getLikes() + 1);

        document = documentService.UpdateDocument(document);
        logger.info("Document liked successfully. New like count: {}", document.getLikes());

        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @PutMapping("/document/dislike/{id}")
    public ResponseEntity<Document> dislikeDocument(@PathVariable Long id) {
        logger.info("Starting dislike operation for document ID: {}", id);

        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (existingDocument.isEmpty()) {
            logger.warn("Document not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Document document = existingDocument.get();
        document.setDislike(document.getDislike() + 1);

        document = documentService.UpdateDocument(document);
        logger.info("Document disliked successfully. New dislike count: {}", document.getDislike());

        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("/auth/document/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return (ResponseEntity<DocumentResponse>) documentService.getDocumentById(id)
                .map(document -> {
                    try {
                        String filePath = document.getFilePath();
                        if (filePath == null || filePath.isEmpty()) {
                            return ResponseEntity.badRequest().build();
                        }

                        File file = new File(filePath);
                        if (file.exists() && file.isFile()) {
                            byte[] fileContent = Files.readAllBytes(file.toPath());
                            DocumentResponse response = new DocumentResponse(document, fileContent);
                            return ResponseEntity.ok(response);
                        }
                        return ResponseEntity.notFound().build();
                    } catch (IOException e) {
                        logger.error("Error reading file for document ID: " + id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/document/{id}/download")
    public ResponseEntity<UrlResource> downloadDocument(@PathVariable Long id) {
        try {
            Optional<Document> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();
            Path filePath = Paths.get(document.getFilePath());
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment()
                                        .filename(filePath.getFileName().toString())
                                        .build().toString())
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for document ID: " + id, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            logger.error("IO error for document ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/auth/document/all")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();
            if (documents.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<DocumentResponse> documentResponses = new ArrayList<>();
            for (Document document : documents) {
                DocumentResponse response = new DocumentResponse();
                response.setDocument(document);

                if (document.getFilePath() != null && !document.getFilePath().isEmpty()) {
                    File file = new File(document.getFilePath());
                    if (file.exists()) {
                        response.setFileContent(Files.readAllBytes(file.toPath()));
                    } else {
                        logger.warn("File not found: {}", document.getFilePath());
                        response.setFileContent(null);
                    }
                } else {
                    logger.warn("Invalid file path for document ID: {}", document.getId());
                    response.setFileContent(null);
                }
                documentResponses.add(response);
            }

            return ResponseEntity.ok(documentResponses);
        } catch (IOException e) {
            logger.error("Error retrieving documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/document/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            Optional<Document> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();
            if (document.getFilePath() != null) {
                Path filePath = Paths.get(document.getFilePath());
                Files.deleteIfExists(filePath);
            }

            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            logger.error("Error deleting document file for ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/auth/document")
    public ResponseEntity<Document> saveDocument(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {

        logger.info("Starting document save operation");

        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (bibliotheque.isEmpty() || type.isEmpty() || utilisateur.isEmpty()) {
            logger.warn("One or more associated entities not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (file.isEmpty()) {
            logger.warn("Uploaded file is empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            String pwd = System.getProperty("user.dir");
            File destination = new File(pwd + "/temp/" + file.getOriginalFilename());
            file.transferTo(destination);
            logger.info("File transferred successfully: {}", destination.getAbsolutePath());

            Document document = new Document();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());
            document.setFilePath(destination.getAbsolutePath());

            document = documentService.saveDocument(document);
            logger.info("Document saved successfully with ID: {}", document.getId());

            return new ResponseEntity<>(document, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("Error processing file", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/auth/document/{id}")
    public ResponseEntity<Document> updateDocument(
            @PathVariable("id") Long id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        logger.info("Starting document update operation for ID: {}", id);

        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (existingDocument.isEmpty()) {
            logger.warn("Document not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);

        if (bibliotheque.isEmpty() || type.isEmpty()) {
            logger.warn("One or more associated entities not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Document document = existingDocument.get();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());

            if (file != null && !file.isEmpty()) {
                logger.info("Updating file for document ID: {}", id);
                String pwd = System.getProperty("user.dir");
                File destination = new File(pwd + "/temp/" + file.getOriginalFilename());
                file.transferTo(destination);
                logger.info("File transferred successfully: {}", destination.getAbsolutePath());
                document.setFilePath(destination.getAbsolutePath());
            }

            document = documentService.UpdateDocument(document);
            logger.info("Document updated successfully with ID: {}", document.getId());

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error processing file for document ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/document/user/{userId}")
    public ResponseEntity<List<Document>> getDocumentsByUser(@PathVariable Long userId) {
        List<Document> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/uploads/documents/{idDocs}")
    public ResponseEntity<UrlResource> downloadDocumentByIdDocs(@PathVariable String idDocs) {
        try {
            Path filePath = Paths.get(DOCUMENTS_DIR).resolve(idDocs).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for document ID: " + idDocs, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            logger.error("IO error for document ID: " + idDocs, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/all")
    public ResponseEntity<List<Type>> getAllTypes() {
        List<Type> types = typeService.getAllTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @GetMapping("/comentaire/all")
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        List<Commentaire> commentaires = commentaireService.getAllCommentaires();
        return new ResponseEntity<>(commentaires, HttpStatus.OK);
    }

    @PostMapping("/auth/commentaire/create")
    public ResponseEntity<Commentaire> createCommentaire(
            @RequestParam("message") String message,
            @RequestParam("documentId") Long documentId,
            @RequestParam("userId") Long userId) {

        Optional<Document> document = documentService.getDocumentById(documentId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (document.isEmpty() || utilisateur.isEmpty()) {
            logger.warn("Document or user not found for comment creation");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setMessage(message);
        commentaire.setDocument(document.get());
        commentaire.setUtilisateur(utilisateur.get());

        Commentaire savedCommentaire = commentaireService.saveCommentaire(commentaire);
        return new ResponseEntity<>(savedCommentaire, HttpStatus.CREATED);
    }

    @PutMapping("/auth/commentaire/update/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(
            @PathVariable Long id,
            @RequestParam("message") String message,
            @RequestParam("documentId") Long documentId,
            @RequestParam("userId") Long userId) {

        Optional<Commentaire> existingCommentaire = commentaireService.findById(id);
        Optional<Document> document = documentService.getDocumentById(documentId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (existingCommentaire.isEmpty() || document.isEmpty() || utilisateur.isEmpty()) {
            logger.warn("Comment, document, or user not found for comment update");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Commentaire commentaire = existingCommentaire.get();
        commentaire.setMessage(message);
        commentaire.setDocument(document.get());
        commentaire.setUtilisateur(utilisateur.get());

        Commentaire updatedCommentaire = commentaireService.saveCommentaire(commentaire);
        return new ResponseEntity<>(updatedCommentaire, HttpStatus.OK);
    }

    @DeleteMapping("/auth/commentaire/delete/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        Optional<Commentaire> commentaire = commentaireService.findById(id);

        if (commentaire.isEmpty()) {
            logger.warn("Comment not found for deletion, ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        commentaireService.deleteCommentaire(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/commentaire/document/{documentId}")
    public ResponseEntity<List<Commentaire>> getCommentairesByDocument(@PathVariable Long documentId) {
        List<Commentaire> commentaires = commentaireService.getCommentairesByDocumentId(documentId);
        return new ResponseEntity<>(commentaires, HttpStatus.OK);
    }
}