package com.Biblio.cours.services;

import com.Biblio.cours.entities.Type;
import java.util.List;
import java.util.Optional;

public interface ITypeService {
    Type  saveType(Type type);
    Type  editType(Long id, Type type);

    // Get all Utilisateurs
    List<Type> getAllTypes();

    // Get Utilisateur by ID
    Optional<Type> getTypeById(Long id);

    // Delete Utilisateur by ID
    void deleteType(Long id);

}

