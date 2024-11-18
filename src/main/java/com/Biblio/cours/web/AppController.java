package com.Biblio.cours.web;

import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.dto.BibliothequeDTO;
import com.Biblio.cours.dto.DocumentResponse;
import com.Biblio.cours.entities.*;
import com.Biblio.cours.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@CrossOrigin("http://localhost:3000")
@RestController
public class AppController {

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

    @PostMapping("/api/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }




    @DeleteMapping("/api/bibliotique/delete/{id}")
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        bibliothequeService.deleteBibliotheque(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/document/search")
    public List<Document> search(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String filier,
            @RequestParam(required = false) String niveaux,
            @RequestParam(required = false) Long bibliothequeId,
            @RequestParam(required = false) Long typeId) {
        return documentService.searchDocuments(titre, description, filier, niveaux, bibliothequeId, typeId);
    }

    @GetMapping("/api/bibliotique/all")
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

    @GetMapping("/api/users/{email}")
    public ResponseEntity<Optional<Utilisateur>> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> user = utilisateurService.getUtilisateurByEmail(email);
        return ResponseEntity.ok(user);
    }


    // like the document
    @PutMapping("/api/document/like/{id}")
    public ResponseEntity<Document> LikeDocument(
            @PathVariable("id") Long id)
           {

        System.out.println("Début de la méthode updateDocument.");

        // Recherche du document existant
        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (!existingDocument.isPresent()) {
            System.out.println("Erreur: Document introuvable avec l'ID: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Document non trouvé
        }



            Document document = existingDocument.get();
            int likes=document.getLikes()+1;
            document.setLikes(likes);
            document.setId(id);





            // Sauvegarder les modifications du document dans la base de données
            document = documentService.UpdateDocument(document);

            // Retourner une réponse réussie avec le document mis à jour
            return new ResponseEntity<>(document, HttpStatus.OK);



    }



      // dislike the document
    @PutMapping("/api/document/dislike/{id}")
    public ResponseEntity<Document> dislikeDocument(@PathVariable Long id) {
        // Recherche du document existant
        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (!existingDocument.isPresent()) {
            System.out.println("Erreur: Document introuvable avec l'ID: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Document non trouvé
        }



        Document document = existingDocument.get();
        int dislikes=document.getDislike()+1;
        document.setDislike(dislikes);
        document.setId(id);





        // Sauvegarder les modifications du document dans la base de données
        document = documentService.UpdateDocument(document);

        // Retourner une réponse réussie avec le document mis à jour
        return new ResponseEntity<>(document, HttpStatus.OK);
    }




    //get document by id
    @GetMapping("/api/document/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        // Retrieve the document from the database
        return (ResponseEntity<DocumentResponse>) documentService.getDocumentById(id)
                .map(document -> {
                    try {
                        // Retrieve the file path from the document entity
                        String filePath = document.getFilePath() ; // Assuming 'getFilePath' gives the correct file path

                        // Validate if the file path is valid
                        if (filePath == null || filePath.isEmpty()) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid file path
                        }

                        // Create the file object
                        File file = new File(filePath);

                        // Check if the file exists
                        if (file.exists() && file.isFile()) {
                            // Read the file content as a byte array
                            byte[] fileContent = Files.readAllBytes(file.toPath());

                            // Return a response with both document data and file content
                            DocumentResponse response = new DocumentResponse(document, fileContent);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // File not found
                        }
                    } catch (IOException e) {
                        // Log the error and return an internal server error
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error reading file
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Document not found in the database
    }

    @GetMapping("/api/document/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Optional<Document> documentOptional = documentService.getDocumentById(id);

        if (documentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document document = documentOptional.get();

        try {
            String filePath = document.getFilePath();
            Path path = Paths.get(filePath);

            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(path);

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(path.getFileName().toString())
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    //get all the document
    @GetMapping("/api/auth/document/all")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();

            if (documents.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No documents found
            }

            List<DocumentResponse> documentResponses = new ArrayList<>();
            for (Document document : documents) {
                DocumentResponse response = new DocumentResponse();
                 response.setDocument(document);

                // Type mapping (assuming you have a 'Type' entity and its mapping)



                // Handle the file content
                if (document.getFilePath() != null && !document.getFilePath().isEmpty()) {
                    File file = new File(document.getFilePath());

                    if (file.exists()) {
                        byte[] fileContent = Files.readAllBytes(file.toPath());
                        response.setFileContent(fileContent); // Assuming DocumentResponse has a field for fileContent
                    } else {
                        System.out.println("File not found: " + document.getFilePath());
                        response.setFileContent(null); // File not found, set to null
                    }
                } else {
                    System.out.println("Invalid file path for document with ID: " + document.getId());
                    response.setFileContent(null); // Invalid file path
                }

                documentResponses.add(response);
            }

            return new ResponseEntity<>(documentResponses, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle server error
        }
    }

    // delete document

    @DeleteMapping("/api/document/delete/{id}")
    public void deleteDocument(@PathVariable Long id) {
        // Fetch the document by ID
        Document document= new Document();
        document=documentService.getDocumentById(id).get();

        // Check if the file path is present
        if (document.getFilePath() != null) {
            try {
                Path filePath = Paths.get(document.getFilePath());
                if(filePath != null) {
                    Files.deleteIfExists(filePath); // Deletes the file if it exists
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file at " + document.getFilePath(), e);
            }
        } else {
            System.out.println("No file associated with this document to delete.");
        }

        // Delete the document from the database
        documentService.deleteDocument(id);
    }


    //save document
    @PostMapping("/api/auth/document")
    public ResponseEntity<Document> saveDocument(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {

        System.out.println("Début de la méthode saveDocument.");

        // Recherche des entités associées
        System.out.println("Recherche des entités associées...");
        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        // Vérification de l'existence des entités associées
        if (!bibliotheque.isPresent() || !type.isPresent() || !utilisateur.isPresent()) {
            System.out.println("Erreur: une ou plusieurs entités associées sont introuvables.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Entités non trouvées
        }

        // Vérification si le fichier est vide
        if (file.isEmpty()) {
            System.out.println("Erreur: le fichier est vide.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Fichier vide
        }

        try {
            // Déterminer le chemin du répertoire de destination
            String pwd = System.getProperty("user.dir");
            System.out.println("Répertoire actuel: " + pwd);

            // Créer le fichier de destination
            File destination = new File(pwd + "/temp/" + file.getOriginalFilename());

            // Sauvegarder le fichier
            file.transferTo(destination);
            System.out.println("Fichier transféré avec succès: " + destination.getAbsolutePath());

            // Si le fichier a été bien transféré, on pourrait ensuite créer et sauvegarder un Document
            Document document = new Document();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());
            document.setFilePath(destination.getAbsolutePath()); // On peut sauvegarder le chemin du fichier ou d'autres informations

            // Sauvegarder le document dans la base de données
            document = documentService.saveDocument(document);  // Assurez-vous que documentDAO est bien injecté et opérationnel

            // Retourner une réponse réussie avec le document créé
            return new ResponseEntity<>(document, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println("Erreur lors du traitement du fichier: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Erreur serveur
        }
    }


    //update document
    @PutMapping("/api/auth/document/{id}")
    public ResponseEntity<Document> updateDocument(
            @PathVariable("id") Long id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("Début de la méthode updateDocument.");

        // Recherche du document existant
        Optional<Document> existingDocument = documentService.getDocumentById(id);
        if (!existingDocument.isPresent()) {
            System.out.println("Erreur: Document introuvable avec l'ID: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Document non trouvé
        }

        // Recherche des entités associées
        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        // Vérification de l'existence des entités associées
        if (!bibliotheque.isPresent() || !type.isPresent() || !utilisateur.isPresent()) {
            System.out.println("Erreur: une ou plusieurs entités associées sont introuvables.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Entités non trouvées
        }

        try {
            Document document = existingDocument.get();
            document.setTitre(titre);
            document.setId(id);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());

            // Si un fichier est fourni, mettre à jour le fichier
            if (file != null && !file.isEmpty()) {
                System.out.println("Mise à jour du fichier...");

                // Déterminer le chemin du répertoire de destination
                String pwd = System.getProperty("user.dir");
                System.out.println("Répertoire actuel: " + pwd);

                // Créer un nouveau fichier de destination
                File destination = new File(pwd + "/temp/" + file.getOriginalFilename());

                // Sauvegarder le fichier
                file.transferTo(destination);
                System.out.println("Fichier transféré avec succès: " + destination.getAbsolutePath());

                // Mettre à jour le chemin du fichier dans le document
                document.setFilePath(destination.getAbsolutePath());
            }

            // Sauvegarder les modifications du document dans la base de données
            document = documentService.UpdateDocument(document);

            // Retourner une réponse réussie avec le document mis à jour
            return new ResponseEntity<>(document, HttpStatus.OK);

        } catch (IOException e) {
            System.out.println("Erreur lors du traitement du fichier: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Erreur serveur
        }
    }





    @GetMapping("/api/document/user/{userId}")
    public ResponseEntity<List<Document>> getDocumentsByUser(@PathVariable Long userId) {
        List<Document> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/uploads/documents/{idDocs}")
    public ResponseEntity<UrlResource> downloadDocument(@PathVariable String idDocs) {
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
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/type/all")
    public ResponseEntity<List<Type>> getAllTypes() {
        List<Type> types = typeService.getAllTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @PostMapping("/api/comentaire/create")
    public ResponseEntity<Commentaire> createCommentaire(@RequestBody Commentaire commentaire) {
        Commentaire savedCommentaire = commentaireService.saveCommentaire(commentaire);
        return new ResponseEntity<>(savedCommentaire, HttpStatus.CREATED);
    }

    @GetMapping("/api/comentaire/{id}")
    public ResponseEntity<Commentaire> getCommentaireById(@PathVariable Long id) {
        Commentaire commentaire = commentaireService.getCommentaireById(id);
        return new ResponseEntity<>(commentaire, HttpStatus.OK);
    }

    @GetMapping("/api/comentaire/all")
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        List<Commentaire> commentaires = commentaireService.getAllCommentaires();
        return new ResponseEntity<>(commentaires, HttpStatus.OK);
    }

    @DeleteMapping("/api/comentaire/delete/{id}")
    public ResponseEntity<String> deleteCommentaire(@PathVariable Long id) {
        commentaireService.deleteCommentaire(id);
        return new ResponseEntity<>("Commentaire supprimé avec succès!", HttpStatus.OK);
    }

    @PutMapping("/api/comentaire/update/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(
            @PathVariable Long id,
            @RequestBody Commentaire updatedCommentaire) {
        Commentaire commentaire = commentaireService.updateCommentaire(id, updatedCommentaire);
        return new ResponseEntity<>(commentaire, HttpStatus.OK);
    }


}