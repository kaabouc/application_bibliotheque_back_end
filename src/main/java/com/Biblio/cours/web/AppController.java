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

    @Autowired
    private ITypeService typeService;

    @Value("${file.upload-dir}")
    private String DOCUMENTS_DIR;

    @PostMapping("/api/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

    @PostMapping("/api/document/save")
    public ResponseEntity<Document> saveDocument(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {

        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

        if (!bibliotheque.isPresent() || !type.isPresent() || !utilisateur.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Document document = new Document();
        document.setTitre(titre);
        document.setDescription(description);
        document.setFilier(filier);
        document.setNiveaux(niveaux);
        document.setBibliotheque(bibliotheque.get());
        document.setType(type.get());
        document.setUtilisateur(utilisateur.get());

        Document savedDocument = documentService.saveDocument(document, file);
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
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

    @GetMapping("/api/document/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(document -> new ResponseEntity<>(document, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/auth/document/all")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

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
            @RequestParam("userId") Long userId) {

        try {
            Optional<Document> existingDoc = documentService.getDocumentById(id);
            if (!existingDoc.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
            Optional<Type> type = typeDAO.findById(typeId);
            Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);

            if (!bibliotheque.isPresent() || !type.isPresent() || !utilisateur.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Document document = existingDoc.get();
            document.setTitre(titre);
            document.setDescription(description);
            document.setFilier(filier);
            document.setNiveaux(niveaux);
            document.setBibliotheque(bibliotheque.get());
            document.setType(type.get());
            document.setUtilisateur(utilisateur.get());

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