package com.Biblio.cours.entities;

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
    private List<Document> documents;

    public Bibliotheque() {
    }

}