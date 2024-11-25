package com.Biblio.cours.services;

import com.Biblio.cours.dao.ContactRepository;
import com.Biblio.cours.entities.Contact;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    @Override
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
