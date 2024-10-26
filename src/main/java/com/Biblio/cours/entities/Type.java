package com.Biblio.cours.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Entity
@Getter
@Setter
@Data
public class Type {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private  String subtitle;
    private  String description;

    @JsonIgnore
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    // Constructors, Getters, and Setters
    public Type() {}

    public Type(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}

