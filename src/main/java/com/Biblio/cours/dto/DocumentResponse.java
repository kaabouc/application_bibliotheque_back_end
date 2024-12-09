package com.Biblio.cours.dto;

import com.Biblio.cours.entities.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DocumentResponse {
    private Document document;
    private byte[] fileContent;

    // Constructor, getters, and setters

    public DocumentResponse(Document document) {
        this.document = document;

    }


}
