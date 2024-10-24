package com.Biblio.cours.web;


import com.Biblio.cours.entities.Bibliotheque;
import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.services.IBibliothequeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:3000")
@RestController
public class AdminController {
    @Autowired
    private com.Biblio.cours.services.IUtilisateurService utilisateurService;
    @Autowired
    private IBibliothequeService bibliothequeService;

    @GetMapping("/api/user/all")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }
    @PostMapping("/api/admin/user/create")
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
    // Create or Update Bibliotheque
    @PostMapping("/api/admin/bibliotique/save")
    public ResponseEntity<Bibliotheque> saveBibliotheque(@RequestBody Bibliotheque bibliotheque) {
        Bibliotheque savedBibliotheque = bibliothequeService.saveBibliotheque(bibliotheque);
        return new ResponseEntity<>(savedBibliotheque, HttpStatus.CREATED);
    }

    // Get all Bibliotheques
    @GetMapping("/api/admin/bibliotique/all")
    public ResponseEntity<List<Bibliotheque>> getAllBibliotheques() {
        List<Bibliotheque> bibliotheques = bibliothequeService.getAllBibliotheques();
        return new ResponseEntity<>(bibliotheques, HttpStatus.OK);
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
}
