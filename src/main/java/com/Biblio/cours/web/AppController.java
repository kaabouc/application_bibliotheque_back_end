package com.Biblio.cours.web;

import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.dto.BibliothequeDTO;
import com.Biblio.cours.dto.DocumentResponse;
import com.Biblio.cours.dto.UtilisateurDTO;
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
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@CrossOrigin(origins = {"http://localhost:3000", "https://e-read-me.onrender.com"})
@RestController
public class AppController {

    @Autowired
    private IContcatService contcatService;

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

    @PostMapping("/api/auth/contact")
    public ResponseEntity<Contact> saveContact(@RequestBody Contact contact) {
        Contact savedContact = contcatService.saveContcat(contact);
        return ResponseEntity.ok(savedContact);
    }



    @DeleteMapping("/api/bibliotique/delete/{id}")
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        bibliothequeService.deleteBibliotheque(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/auth/document/search")
    public ResponseEntity<List<Document>> searchDocuments(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) List<Long> bibliotheques,
            @RequestParam(required = false) List<Long> types,
            @RequestParam(required = false) List<String> filier,
            @RequestParam(required = false) List<String> niveaux) {

        List<Document> documents = documentService.searchDocuments(titre, bibliotheques, types, filier, niveaux);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/api/bibliotique/all")
    public ResponseEntity<List<BibliothequeDTO>> getAllBibliotheques() {
        List<Bibliotheque> bibliotheques = bibliothequeService.getAllBibliotheques();
        List<BibliothequeDTO> dtos = bibliotheques.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @GetMapping("/api/auth/bibliotique/all")
    public ResponseEntity<List<BibliothequeDTO>> getAllBibliothequesForVisitor() {
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

//    @GetMapping("/api/users/{email}")
//    public ResponseEntity<Optional<Utilisateur>> getUtilisateurByEmail(@PathVariable String email) {
//        Optional<Utilisateur> user = utilisateurService.getUtilisateurByEmail(email);
//        return ResponseEntity.ok(user);
//    }



    @GetMapping("/api/users/{email}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> userOptional = utilisateurService.getUtilisateurByEmail(email);

        if (userOptional.isPresent()) {
            Utilisateur utilisateur = userOptional.get();
            String imagePath = utilisateur.getImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    try {
                        byte[] imageContent = Files.readAllBytes(imageFile.toPath());
                        String encodedImage = Base64.getEncoder().encodeToString(imageContent);
                        utilisateur.setImage(encodedImage); // Store the Base64-encoded image
                    } catch (IOException e) {
                        System.out.println("Error reading image: " + e.getMessage());
                        utilisateur.setImage(null);
                    }
                } else {
                    System.out.println("Image not found: " + imagePath);
                    utilisateur.setImage(null);
                }
            } else {
                System.out.println("Invalid image path for user: " + utilisateur.getEmail());
                utilisateur.setImage(null);
            }

            UtilisateurDTO utilisateurDTO = new UtilisateurDTO(utilisateur); // Assume UtilisateurDTO handles the necessary fields.
            return ResponseEntity.ok(utilisateurDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
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



    @GetMapping("/api/auth/document/{id}/view")
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long id) {
        // Récupérer le document par ID
        Optional<Document> documentOptional = documentService.getDocumentById(id);

        if (documentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document document = documentOptional.get();

        // Vérifier si le fichier existe dans l'objet Document
        byte[] fileContent = document.getFile(); // Contient le fichier
        if (fileContent == null || fileContent.length == 0) {
            return ResponseEntity.notFound().build();
        }

        // Utiliser un type de contenu par défaut (PDF dans cet exemple)
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; // Type générique
        // Vous pouvez détecter le type si nécessaire (exemple avec un PDF par défaut)

        // Définir un nom de fichier par défaut
        String fileName = "document_" + id;

        // Construire les en-têtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.add("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        // Retourner le fichier
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

//    @GetMapping("/api/auth/document/{id}")
//    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
//        // Retrieve the document from the service
//        Optional<Document> documentOptional = documentService.getDocumentById(id);
//
//        if (documentOptional.isEmpty()) {
//            return ResponseEntity.notFound().build(); // Document not found
//        }
//
//        Document document = documentOptional.get();
//
//        try {
//            // Ensure that the document has a file
//            byte[] fileContent = document.getFile();
//            if (fileContent == null || fileContent.length == 0) {
//                return ResponseEntity.notFound().build(); // File not found
//            }
//
//            // Set the Content-Disposition header
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF); // Assuming PDF for now
//            headers.setContentDisposition(ContentDisposition.builder("attachment")
//                    .filename(document.getTitre() + document.getId() + ".pdf") // Filename with .pdf extension
//                    .build());
//
//            // Return the file content with appropriate headers
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(new DocumentResponse(document, fileContent));
//
//        } catch (Exception e) {
//            // Log the error and provide a more detailed response
//           e.getMessage();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
@GetMapping("/api/auth/document/{id}/file")
public ResponseEntity<byte[]> getDocumentFile(@PathVariable Long id) {
    // Retrieve the document from the service
    Optional<Document> documentOptional = documentService.getDocumentById(id);

    if (documentOptional.isEmpty()) {
        return ResponseEntity.notFound().build(); // Document not found
    }

    Document document = documentOptional.get();

    try {
        // Ensure that the document has a file
        byte[] fileContent = document.getFile();
        if (fileContent == null || fileContent.length == 0) {
            return ResponseEntity.notFound().build(); // File not found
        }

        // Set the Content-Disposition header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // Assuming PDF for now
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(document.getTitre() + document.getId() + ".pdf") // Filename with .pdf extension
                .build());

        // Return the file content with appropriate headers
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);

    } catch (Exception e) {
        // Log the error and provide a more detailed response
        e.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
    @GetMapping("/api/auth/document/{id}/metadata")
    public ResponseEntity<DocumentResponse> getDocumentMetadata(@PathVariable Long id) {
        // Retrieve the document from the service
        Optional<Document> documentOptional = documentService.getDocumentById(id);

        if (documentOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); // Document not found
        }

        Document document = documentOptional.get();

        // Create a response containing only the document metadata
        DocumentResponse response = new DocumentResponse(document);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/document/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Optional<Document> documentOptional = documentService.getDocumentById(id);

        if (documentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document document = documentOptional.get();

        // Assuming the document has a 'file' field that holds the byte content
        byte[] fileContent = document.getFile();

        if (fileContent == null || fileContent.length == 0) {
            return ResponseEntity.notFound().build();
        }

        // Set content type explicitly to application/pdf
        String contentType = "application/pdf";

        // Set file name with .pdf extension
        String fileName = document.getTitre() + ".pdf"; // Assuming `getTitre()` returns the document title

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileName) // Ensure .pdf extension
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(fileContent);
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

            Document document = new Document();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());
            document.setFile(file.getBytes());

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
            // @RequestParam("userId") Long userId,
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
        //   Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        // Vérification de l'existence des entités associées
        if (!bibliotheque.isPresent() || !type.isPresent() ) {
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
            // document.setUtilisateur(utilisateur.get());

            // Si un fichier est fourni, mettre à jour le fichier
            if (file != null && !file.isEmpty()) {
              document.setFile(file.getBytes());
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
    @GetMapping("/api/auth/type/all")
    public ResponseEntity<List<Type>> getAllTypesForVisitor() {
        List<Type> types = typeService.getAllTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @GetMapping("/api/comentaire/all")
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        List<Commentaire> commentaires = commentaireService.getAllCommentaires();
        return new ResponseEntity<>(commentaires, HttpStatus.OK);
    }


    @PostMapping("/api/auth/commentaire/create")
    public ResponseEntity<Commentaire> createCommentaire(
            @RequestParam("message") String message,
            @RequestParam("documentId") Long documentId,
            @RequestParam("userId") Long userId) {

        Optional<Document> document = documentService.getDocumentById(documentId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (!document.isPresent() || !utilisateur.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setMessage(message);
        commentaire.setDocument(document.get());
        commentaire.setUtilisateur(utilisateur.get());

        Commentaire savedCommentaire = commentaireService.saveCommentaire(commentaire);
        return new ResponseEntity<>(savedCommentaire, HttpStatus.CREATED);
    }

    @PutMapping("/api/auth/commentaire/update/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(
            @PathVariable Long id,
            @RequestParam("message") String message,
            @RequestParam("documentId") Long documentId,
            @RequestParam("userId") Long userId) {

        Optional<Commentaire> existingCommentaire = commentaireService.findById(id);
        Optional<Document> document = documentService.getDocumentById(documentId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (!existingCommentaire.isPresent() || !document.isPresent() || !utilisateur.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Commentaire commentaire = existingCommentaire.get();
        commentaire.setMessage(message);
        commentaire.setDocument(document.get());
        commentaire.setUtilisateur(utilisateur.get());

        Commentaire updatedCommentaire = commentaireService.saveCommentaire(commentaire);
        return new ResponseEntity<>(updatedCommentaire, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/commentaire/delete/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        Optional<Commentaire> commentaire = commentaireService.findById(id);

        if (!commentaire.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        commentaireService.deleteCommentaire(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/auth/commentaire/document/{documentId}")
    public ResponseEntity<List<Commentaire>> getCommentairesByDocument(@PathVariable Long documentId) {
        List<Commentaire> commentaires = commentaireService.getCommentairesByDocumentId(documentId);
        return new ResponseEntity<>(commentaires, HttpStatus.OK);
    }


    @PutMapping("/api/user/updateProfile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile image) {

        // Fetch the user by ID
        Optional<Utilisateur> optionalUtilisateur = utilisateurService.getUtilisateurById(id);
        if (!optionalUtilisateur.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Utilisateur utilisateur = optionalUtilisateur.get();

        // Update the name if provided
        if (nom != null && !nom.isEmpty()) {
            utilisateur.setNom(nom);
        }

        // Update the email if provided
        if (email != null && !email.isEmpty()) {
            utilisateur.setEmail(email);
        }

        // Update the profile image if provided
        if (image != null && !image.isEmpty()) {
            String pwd = System.getProperty("user.dir");
            System.out.println("Current directory: " + pwd);

            // Ensure the folder exists, create if necessary
            File directory = new File(pwd + "/temp");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create directory for image storage.");
                }
            }

            // Create a file path and store the image in the destination folder
            String imagePath = "/temp/" + image.getOriginalFilename();
            File destination = new File(directory, image.getOriginalFilename());

            try {
                // Save the file to the destination folder
                image.transferTo(destination);
                utilisateur.setImagePath(pwd + imagePath); // Assuming `imagePath` is a field in the `Utilisateur` entity
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving the image: " + e.getMessage());
            }
        }

        // Save the updated user
        Utilisateur updatedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);

        // Return the updated user
        return new ResponseEntity<>(updatedUtilisateur, HttpStatus.OK);
    }

    // Save the updated user





}