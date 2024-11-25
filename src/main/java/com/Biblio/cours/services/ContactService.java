package com.Biblio.cours.services;

import com.Biblio.cours.entities.Contact;
import java.util.List;

public interface ContactService {
    Contact saveContact(Contact contact);
    List<Contact> getAllContacts();
    void deleteContact(Long id);
}