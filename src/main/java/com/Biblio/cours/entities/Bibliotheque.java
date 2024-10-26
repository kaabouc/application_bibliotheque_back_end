package com.Biblio.cours.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


import java.util.List;
@Setter
@Getter
@Entity
public class Bibliotheque {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String location;

    @OneToMany(mappedBy = "bibliotheque", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Add this annotation
    @JsonIgnore
    private List<Document> documents;

    public Bibliotheque() {
    }

}