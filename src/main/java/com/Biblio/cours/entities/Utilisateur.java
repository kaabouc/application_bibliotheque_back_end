package com.Biblio.cours.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Getter
@Setter
public class Utilisateur {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String type ; // Admin, Client, etc.
    private String password;

    @Lob
    private byte[] image; // To store profile image as a blob

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Commentaire> commentaires;
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Document> documents;


    // Constructors, Getters, and Setters
    public Utilisateur() {}

    public Utilisateur(String nom, String email, String type, String password, byte[] image) {
        this.nom = nom;
        this.email = email;
        this.type = type;
        this.password = password;
        this.image = image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
