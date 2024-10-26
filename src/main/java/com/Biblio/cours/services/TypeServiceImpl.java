package com.Biblio.cours.services;

import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.entities.Type;
import com.Biblio.cours.entities.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class TypeServiceImpl implements ITypeService{

    @Autowired
    private TypeDAO typeDAO;
    @Override
    public Type saveType(Type type) {
        return typeDAO.save(type);
    }

    @Override
    public Type editType(Long id, Type type) {
         Optional<Type> selectType = typeDAO.findById(id);

        Type typeselect = selectType.get();
        if (type.getName() != null) typeselect.setName(type.getName());
        if (type.getDescription() != null) typeselect.setDescription(type.getDescription());
        if (type.getSubtitle() != null) typeselect.setSubtitle(type.getSubtitle());


        Type updateType = typeDAO.save(typeselect);
        return updateType;
    }

    @Override
    public List<Type> getAllTypes() {
        return typeDAO.findAll();
    }

    @Override
    public Optional<Type> getTypeById(Long id) {
        Optional<Type> optionalType = typeDAO.findById(id);
        return Optional.ofNullable(optionalType.orElse(null));
    }

    @Override
    public void deleteType(Long id) {
        typeDAO.deleteById(id);

    }


}
