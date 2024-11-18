package com.Biblio.cours.dao;

import com.Biblio.cours.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurDAO extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    // Search by name or email
    List<Utilisateur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email);

    // Filter by type
    List<Utilisateur> findByType(String type);

    // Combine search by name/email and filter by type
    List<Utilisateur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndType(String nom, String email, String type);
}