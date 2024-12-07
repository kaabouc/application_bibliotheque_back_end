package com.Biblio.cours.services;

import com.Biblio.cours.entities.Contact;
import com.Biblio.cours.entities.Type;

import java.util.List;

public interface IContcatService {
    List<Contact> getAllContcats();
    Contact saveContcat(Contact contact);
    void deleteContact(Long id);

}
