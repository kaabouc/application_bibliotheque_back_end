package com.Biblio.cours.services;

import com.Biblio.cours.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface IUtilisateurService {
    // Create or Update Utilisateur
    Utilisateur saveUtilisateur(Utilisateur utilisateur);

    // Get all Utilisateurs
    List<Utilisateur> getAllUtilisateurs();

    // Get Utilisateur by ID
    Optional<Utilisateur> getUtilisateurById(Long id);

    // Delete Utilisateur by ID
    void deleteUtilisateur(Long id);

    Optional<Utilisateur> getUtilisateurByEmail(String email);

    public List<Utilisateur> searchByNomOrEmail(String keyword);
    public List<Utilisateur> filterByType(String type);
    public List<Utilisateur> searchByNomOrEmailAndType(String keyword, String type);
}
