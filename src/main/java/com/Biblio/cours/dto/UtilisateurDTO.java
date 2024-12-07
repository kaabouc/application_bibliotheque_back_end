package com.Biblio.cours.dto;

import com.Biblio.cours.entities.Utilisateur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String email;
    private String type;
    private String imagePath;

    // Constructor to convert Utilisateur to UtilisateurDTO
    public UtilisateurDTO(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.nom = utilisateur.getNom();
        this.email = utilisateur.getEmail();
        this.type = utilisateur.getType();
        this.imagePath=utilisateur.getImage();

    }

    // Getters and setters (optional)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

