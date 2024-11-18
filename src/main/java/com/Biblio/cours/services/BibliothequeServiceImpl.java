package com.Biblio.cours.services;

import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.entities.Bibliotheque;

import com.Biblio.cours.entities.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BibliothequeServiceImpl implements IBibliothequeService {

    @Autowired
    private BibliothequeDAO bibliothequeDao;



    @Override
    public Bibliotheque saveBibliotheque(Bibliotheque bibliotheque) {
        return bibliothequeDao.save(bibliotheque);
    }

    @Override
    public List<Bibliotheque> getAllBibliotheques() {
        return bibliothequeDao.findAll();
    }

    @Override
    public Optional<Bibliotheque> getBibliothequeById(Long id) {
        return bibliothequeDao.findById(id);
    }

    @Override
    public void deleteBibliotheque(Long id) {
        bibliothequeDao.deleteById(id);
    }


}
