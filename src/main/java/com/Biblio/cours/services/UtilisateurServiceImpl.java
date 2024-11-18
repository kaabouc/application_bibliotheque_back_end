package com.Biblio.cours.services;


import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Utilisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements com.Biblio.cours.services.IUtilisateurService {

    @Autowired
    private UtilisateurDAO utilisateurDao;

    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurDao.save(utilisateur);
    }

    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurDao.findAll();
    }

    @Override
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurDao.findById(id);
    }

    @Override
    public void deleteUtilisateur(Long id) {
        utilisateurDao.deleteById(id);
    }

    @Override
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurDao.findByEmail(email);
    }
    @Override
    public List<Utilisateur> searchByNomOrEmail(String keyword) {
        return utilisateurDao.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
    @Override
    public List<Utilisateur> filterByType(String type) {
        return utilisateurDao.findByType(type);
    }
    @Override
    public List<Utilisateur> searchByNomOrEmailAndType(String keyword, String type) {
        return utilisateurDao.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndType(keyword, keyword, type);
    }
}

