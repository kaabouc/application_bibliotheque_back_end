package com.Biblio.cours.dto;

import com.Biblio.cours.entities.Document;
import lombok.NoArgsConstructor;



public class DocumentResponse {
    private Document document;
    private byte[] fileContent;

    // Constructor, getters, and setters

    public DocumentResponse(Document document, byte[] fileContent) {
        this.document = document;
        this.fileContent = fileContent;
    }
    public  DocumentResponse(){};

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}

