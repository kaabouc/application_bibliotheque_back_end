package com.Biblio.cours.web;



import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.*;
import com.Biblio.cours.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("/api/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

    // Create or Update Document
    @PostMapping("/api/document/save")
    public ResponseEntity<Document> saveDocument(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("filier") String filier,
            @RequestParam("niveaux") String niveaux,
            @RequestParam("bibliothequeId") Long bibliothequeId,
            @RequestParam("typeId") Long typeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId
    ) {
        // Find related entities using their services
        Optional<Bibliotheque> bibliotheque = bibliothequeDAO.findById(bibliothequeId);
        Optional<Type> type = typeDAO.findById(typeId);
        Optional<Utilisateur> utilisateur = utilisateurDAO.findById(userId);


        // Create a new Document instance and set its properties
        Document document = new Document();
        document.setTitre(titre);
        document.setDescription(description);
        document.setFilier(filier);
        document.setNiveaux(niveaux);
        document.setBibliotheque(bibliotheque.get()); // Set the bibliotheque
        document.setType(type.get()); // Set the type
        document.setUtilisateur(utilisateur.get());

        // Save the document using the service
        Document savedDocument = documentService.saveDocument(document, file);

        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }


    // Get all Documents



    // Get Document by ID
    @GetMapping("/api/document/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Optional<Document> document = documentService.getDocumentById(id);
        return document.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/users/{email}")
    public ResponseEntity<Optional<Utilisateur>> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> user = utilisateurService.getUtilisateurByEmail(email);
        return ResponseEntity.ok(user);
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

    @GetMapping("/api/document/user/{userId}")
    public ResponseEntity<List<Document>> getDocumentsByUser(@PathVariable Long userId) {
        List<Document> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }




}

