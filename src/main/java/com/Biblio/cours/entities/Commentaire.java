package com.Biblio.cours.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
@Getter
@Setter
@Entity
public class Commentaire {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "document_id", nullable = false)
//    @JsonIgnore
    private Document document;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
//    @JsonIgnore
    private Utilisateur utilisateur;

    // Constructors, Getters, and Setters
    public Commentaire() {}

    public Commentaire(String message, Document document, Utilisateur utilisateur) {
        this.message = message;
        this.document = document;
        this.utilisateur = utilisateur;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // Getters and Setters
}

