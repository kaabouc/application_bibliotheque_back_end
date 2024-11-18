package services;



import com.Biblio.cours.dao.CommentaireDAO;
import com.Biblio.cours.entities.Commentaire;
import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.services.CommentaireServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentaireServiceTest {

    @Mock
    private CommentaireDAO commentaireRepository;

    @InjectMocks
    private  CommentaireServiceImpl commentaireService;


    @Test
    public void testSaveCommentaire() {
        // Arrange
        Document document = new Document(); // assume Document class exists
        Utilisateur utilisateur = new Utilisateur(); // assume Utilisateur class exists
        Commentaire commentaire = new Commentaire("Sample message", document, utilisateur);

        when(commentaireRepository.save(commentaire)).thenReturn(commentaire);

        // Act
        Commentaire result = commentaireService.saveCommentaire(commentaire);

        // Assert
        assertNotNull(result);
        assertEquals("Sample message", result.getMessage());
        verify(commentaireRepository, times(1)).save(commentaire);
    }

    @Test
    public void testGetCommentaireById() {
        // Arrange
        Long id = 1L;
        Document document = new Document();
        Utilisateur utilisateur = new Utilisateur();
        Commentaire commentaire = new Commentaire("Sample message", document, utilisateur);

        when(commentaireRepository.findById(id)).thenReturn(Optional.of(commentaire));

        // Act
        Commentaire result = commentaireService.getCommentaireById(id);

        // Assert
        assertNotNull(result);
        assertEquals("Sample message", result.getMessage());
        verify(commentaireRepository, times(1)).findById(id);
    }

    @Test
    public void testGetAllCommentaires() {
        // Arrange
        Document document = new Document();
        Utilisateur utilisateur = new Utilisateur();
        Commentaire commentaire1 = new Commentaire("Message 1", document, utilisateur);
        Commentaire commentaire2 = new Commentaire("Message 2", document, utilisateur);

        List<Commentaire> mockList = Arrays.asList(commentaire1, commentaire2);
        when(commentaireRepository.findAll()).thenReturn(mockList);

        // Act
        List<Commentaire> result = commentaireService.getAllCommentaires();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Message 1", result.get(0).getMessage());
        assertEquals("Message 2", result.get(1).getMessage());
        verify(commentaireRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteCommentaire() {
        // Arrange
        Long id = 1L;

        // Act
        commentaireService.deleteCommentaire(id);

        // Assert
        verify(commentaireRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateCommentaire() {
        // Arrange
        Long id = 1L;
        Document document = new Document();
        Utilisateur utilisateur = new Utilisateur();

        Commentaire existingCommentaire = new Commentaire("Old message", document, utilisateur);
        Commentaire updatedCommentaire = new Commentaire("Updated message", document, utilisateur);

        when(commentaireRepository.findById(id)).thenReturn(Optional.of(existingCommentaire));
        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(updatedCommentaire);

        // Act
        Commentaire result = commentaireService.updateCommentaire(id, updatedCommentaire);

        // Assert
        assertNotNull(result);
        assertEquals("Updated message", result.getMessage());
        verify(commentaireRepository, times(1)).findById(id);
        verify(commentaireRepository, times(1)).save(existingCommentaire);
    }
}

