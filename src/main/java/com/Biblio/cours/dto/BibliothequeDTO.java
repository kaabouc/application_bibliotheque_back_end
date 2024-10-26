package com.Biblio.cours.dto;

import lombok.Data;

@Data
 public class   BibliothequeDTO {
    private Long id;
    private String nom;
    private String location;
    private int documentsCount;
}
