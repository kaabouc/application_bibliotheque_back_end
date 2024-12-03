package com.Biblio.cours.services;

import com.Biblio.cours.dao.ContactRepository;
import com.Biblio.cours.entities.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContactServiceImpl implements IContcatService {
    @Autowired
    public ContactRepository contactRepository;
    @Override
    public List<Contact> getAllContcats() {
        return contactRepository.findAll();
    }

    @Override
    public Contact saveContcat(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
