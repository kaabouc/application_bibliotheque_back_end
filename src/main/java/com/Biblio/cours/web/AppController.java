package com.Biblio.cours.web;



import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.dto.BibliothequeDTO;
import com.Biblio.cours.entities.*;
import com.Biblio.cours.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${file.upload-dir}")
    private String DOCUMENTS_DIR ;


    @Autowired
    private ITypeService typeService;

    @PostMapping("/api/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

    // Create or Update Document

    @DeleteMapping("/api/bibliotique/delete/{id}")
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        bibliothequeService.deleteBibliotheque(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get all Documents


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

    // Get Document by ID
    @GetMapping("/api/document/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/auth/document/all")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    // Delete Document by ID
    @DeleteMapping("/api/document/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/api/document/{id}")
    public ResponseEntity<Document> updateDocument(
            @PathVariable Long id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("userId") Long userId
    ) {
        try {
            // Vérifier si le document existe
            Optional<Document> existingDoc = documentService.getDocumentById(id);
            if (!existingDoc.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Récupérer les entités liées
            Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
            Optional<Type> type = typeDAO.findById(typeId);
            Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

            if (!bibliotheque.isPresent() || !type.isPresent() || !utilisateur.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Mettre à jour le document existant
            Document document = existingDoc.get();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());

            // Sauvegarder le document mis à jour
            Document updatedDocument = documentService.saveDocument(document, file);
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            // Construire le chemin du fichier
            Path filePath = Paths.get(DOCUMENTS_DIR).resolve(idDocs).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Obtenir le type MIME du fichier
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // Type par défaut si non déterminé
                }

                // Retourner le fichier en tant que ressource téléchargeable
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/api/type/all")
    public ResponseEntity<List<Type>> getAllTypes() {
        List<Type> types = typeService.getAllTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @PostMapping("/api/comentaire/create")
    public Commentaire createCommentaire(@RequestBody Commentaire commentaire) {
        return commentaireService.saveCommentaire(commentaire);
    }

    @GetMapping("/api/comentaire/{id}")
    public Commentaire getCommentaireById(@PathVariable Long id) {
        return commentaireService.getCommentaireById(id);
    }

    @GetMapping("/api/comentaire/all")
    public List<Commentaire> getAllCommentaires() {
        return commentaireService.getAllCommentaires();
    }

    @DeleteMapping("/api/comentaire/delete/{id}")
    public String deleteCommentaire(@PathVariable Long id) {
        commentaireService.deleteCommentaire(id);
        return "Commentaire supprimé avec succès!";
    }

    @PutMapping("/api/comentaire/update/{id}")
    public Commentaire updateCommentaire(@PathVariable Long id, @RequestBody Commentaire updatedCommentaire) {
        return commentaireService.updateCommentaire(id, updatedCommentaire);
    }






}
