package com.Biblio.cours.web;


import com.Biblio.cours.dto.BibliothequeDTO;
import com.Biblio.cours.dto.UtilisateurDTO;
import com.Biblio.cours.entities.Bibliotheque;
import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Type;
import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.services.IBibliothequeService;
import com.Biblio.cours.services.IDocumentService;
import com.Biblio.cours.services.ITypeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;



@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS})
public class AdminController {
    @Autowired
    private com.Biblio.cours.services.IUtilisateurService utilisateurService;
    @Autowired
    private IBibliothequeService bibliothequeService;
    @Autowired
    private IDocumentService documentService;

    @Autowired
    private ITypeService typeService;

    @GetMapping("/api/user/test")
    public ResponseEntity<String> testSerialization() throws JsonProcessingException {
        Utilisateur user = utilisateurService.getAllUtilisateurs().get(0);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PostMapping("/api/user/create")
    public ResponseEntity<Utilisateur> saveUtilisateurs(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(savedUtilisateur, HttpStatus.CREATED);
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/api/user/update/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) String password,
                                                         @RequestParam(required = false) String userType,
                                                         @RequestParam(required = false) MultipartFile image) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurService.getUtilisateurById(id);
        if (!optionalUtilisateur.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Utilisateur utilisateur = optionalUtilisateur.get();
        if (name != null) utilisateur.setNom(name);
        if (email != null) utilisateur.setEmail(email);
        if (password != null) utilisateur.setPassword(password); // Ensure proper hashing
        if (userType != null) utilisateur.setType(userType);
        // Handle image upload logic here if necessary

        Utilisateur updatedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return new ResponseEntity<>(updatedUtilisateur, HttpStatus.OK);
    }


    @DeleteMapping("/api/admin/user/delete/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/api/user/all")
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        List<UtilisateurDTO> utilisateurs = utilisateurService.getAllUtilisateurs()
                .stream()
                .map(UtilisateurDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }
    // Create or Update Bibliotheque
    @PostMapping("/api/admin/bibliotique/save")
    public ResponseEntity<Bibliotheque> saveBibliotheque(@RequestBody Bibliotheque bibliotheque) {
        Bibliotheque savedBibliotheque = bibliothequeService.saveBibliotheque(bibliotheque);
        return new ResponseEntity<>(savedBibliotheque, HttpStatus.CREATED);
     }

    @GetMapping("/api/admin/bibliotique/all")
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

    // Get Bibliotheque by ID
    @GetMapping("/api/admin/bibliotique/{id}")
    public ResponseEntity<Bibliotheque> getBibliothequeById(@PathVariable Long id) {
        Optional<Bibliotheque> bibliotheque = bibliothequeService.getBibliothequeById(id);
        return bibliotheque.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // Delete Bibliotheque by ID
    @DeleteMapping("/api/admin/bibliotique/delete/{id}")
    public ResponseEntity<Void> deleteBibliotheque(@PathVariable Long id) {
        bibliothequeService.deleteBibliotheque(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/api/admin/bibliotique/update/{id}")
    public ResponseEntity<Bibliotheque> updateBibliotheque(@PathVariable Long id,
                                                           @RequestParam(required = false) String nom,
                                                           @RequestParam(required = false) String location) {
        Optional<Bibliotheque> optionalBibliotheque = bibliothequeService.getBibliothequeById(id);

        if (!optionalBibliotheque.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Bibliotheque bibliotheque = optionalBibliotheque.get();

        // Update fields if provided
        if (nom != null) bibliotheque.setNom(nom);
        if (location != null) bibliotheque.setLocation(location);



        // Save updated bibliotheque
        Bibliotheque updatedBibliotheque = bibliothequeService.saveBibliotheque(bibliotheque);
        return new ResponseEntity<>(updatedBibliotheque, HttpStatus.OK);
    }
    @GetMapping("/api/admin/document/all")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }
    @GetMapping("/api/admin/document/user/{userId}")
    public ResponseEntity<List<Document>> getDocumentsByUser(@PathVariable Long userId) {
        List<Document> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }
    @GetMapping("/api/admin/users/{email}")
    public ResponseEntity<Optional<Utilisateur>> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> user = utilisateurService.getUtilisateurByEmail(email);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/api/admin/document/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("/api/admin/type")
    public ResponseEntity<Type> createType(@RequestBody Type type) {
        Type savedType = typeService.saveType(type);
        return new ResponseEntity<>(savedType, HttpStatus.CREATED);
    }

    @PutMapping("/api/admin/type/{id}")
    public ResponseEntity<Type> updateType(@PathVariable Long id, @RequestBody Type type) {
        try {
            Type updatedType = typeService.editType(id, type);
            return new ResponseEntity<>(updatedType, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/admin/type")
    public ResponseEntity<List<Type>> getAllTypes() {
        List<Type> types = typeService.getAllTypes();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @GetMapping("/api/admin/type/{id}")
    public ResponseEntity<Type> getTypeById(@PathVariable Long id) {
        Optional<Type> type = typeService.getTypeById(id);
        return type.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/api/admin/type/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        try {
            typeService.deleteType(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/admin/user/search")
    public List<Utilisateur> searchByNomOrEmail(@RequestParam String keyword) {
        return utilisateurService.searchByNomOrEmail(keyword);
    }

    // Filter by type
    @GetMapping("/api/admin/user/filter")
    public List<Utilisateur> filterByType(@RequestParam String type) {
        return utilisateurService.filterByType(type);
    }

    // Search by name or email and filter by type
    @GetMapping("/api/admin/user/search-filter")
    public List<Utilisateur> searchByNomOrEmailAndType(@RequestParam String keyword, @RequestParam String type) {
        return utilisateurService.searchByNomOrEmailAndType(keyword, type);
    }
}