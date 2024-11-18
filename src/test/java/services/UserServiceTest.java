package services;

import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.services.UtilisateurServiceImpl;
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
public class UserServiceTest {

    @Mock
    UtilisateurDAO utilisateurDAO;

    @InjectMocks
    UtilisateurServiceImpl utilisateurService;

    @Test
    public void testGetAllUser() {
        // Arrange
        Utilisateur user1 = new Utilisateur("Alice", "alice@example.com", "Client", "password1", null);
        Utilisateur user2 = new Utilisateur("Bob", "bob@example.com", "Admin", "password2", null);
        List<Utilisateur> mockUserList = Arrays.asList(user1, user2);

        when(utilisateurDAO.findAll()).thenReturn(mockUserList);

        // Act
        List<Utilisateur> result = utilisateurService.getAllUtilisateurs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getNom());
        assertEquals("Bob", result.get(1).getNom());

        verify(utilisateurDAO, times(1)).findAll();
    }

    @Test
    public void testSaveUtilisateur() {
        // Arrange
        Utilisateur utilisateur = new Utilisateur("Charlie", "charlie@example.com", "Client", "password3", null);
        when(utilisateurDAO.save(utilisateur)).thenReturn(utilisateur);

        // Act
        Utilisateur result = utilisateurService.saveUtilisateur(utilisateur);

        // Assert
        assertNotNull(result);
        assertEquals("Charlie", result.getNom());
        assertEquals("charlie@example.com", result.getEmail());

        verify(utilisateurDAO, times(1)).save(utilisateur);
    }

    @Test
    public void testGetUtilisateurById() {
        // Arrange
        Long userId = 1L;
        Utilisateur utilisateur = new Utilisateur("David", "david@example.com", "Client", "password4", null);
        when(utilisateurDAO.findById(userId)).thenReturn(Optional.of(utilisateur));

        // Act
        Optional<Utilisateur> result = utilisateurService.getUtilisateurById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("David", result.get().getNom());
        verify(utilisateurDAO, times(1)).findById(userId);
    }

    @Test
    public void testDeleteUtilisateur() {
        // Arrange
        Long userId = 2L;

        // Act
        utilisateurService.deleteUtilisateur(userId);

        // Assert
        verify(utilisateurDAO, times(1)).deleteById(userId);
    }

    @Test
    public void testGetUtilisateurByEmail() {
        // Arrange
        String email = "eve@example.com";
        Utilisateur utilisateur = new Utilisateur("Eve", email, "Admin", "password5", null);
        when(utilisateurDAO.findByEmail(email)).thenReturn(Optional.of(utilisateur));

        // Act
        Optional<Utilisateur> result = utilisateurService.getUtilisateurByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Eve", result.get().getNom());
        verify(utilisateurDAO, times(1)).findByEmail(email);
    }
}
