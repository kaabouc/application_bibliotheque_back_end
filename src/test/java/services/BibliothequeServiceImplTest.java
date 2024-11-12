package services;



import com.Biblio.cours.dao.BibliothequeDAO;
import com.Biblio.cours.entities.Bibliotheque;
import com.Biblio.cours.services.BibliothequeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BibliothequeServiceImplTest {

    @Mock
    private BibliothequeDAO bibliothequeDao;

    @InjectMocks
    private BibliothequeServiceImpl bibliothequeService;

    @Test
    public void testSaveBibliotheque() {
        // Arrange
        Bibliotheque bibliotheque = new Bibliotheque();
        bibliotheque.setNom("Central Library");
        Mockito.when(bibliothequeDao.save(bibliotheque)).thenReturn(bibliotheque);

        // Act
        Bibliotheque savedBibliotheque = bibliothequeService.saveBibliotheque(bibliotheque);

        // Assert
        Assertions.assertNotNull(savedBibliotheque);
        Assertions.assertEquals("Central Library", savedBibliotheque.getNom());
    }

    @Test
    public void testGetAllBibliotheques() {
        // Arrange
        Bibliotheque b1 = new Bibliotheque();
        b1.setNom("Library 1");
        Bibliotheque b2 = new Bibliotheque();
        b2.setNom("Library 2");
        List<Bibliotheque> bibliotheques = Arrays.asList(b1, b2);
        Mockito.when(bibliothequeDao.findAll()).thenReturn(bibliotheques);

        // Act
        List<Bibliotheque> result = bibliothequeService.getAllBibliotheques();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Library 1", result.get(0).getNom());
        Assertions.assertEquals("Library 2", result.get(1).getNom());
    }

    @Test
    public void testGetBibliothequeById() {
        // Arrange
        Long id = 1L;
        Bibliotheque bibliotheque = new Bibliotheque();
        bibliotheque.setId(id);
        bibliotheque.setNom("Specific Library");
        Mockito.when(bibliothequeDao.findById(id)).thenReturn(Optional.of(bibliotheque));

        // Act
        Optional<Bibliotheque> result = bibliothequeService.getBibliothequeById(id);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Specific Library", result.get().getNom());
    }

    @Test
    public void testDeleteBibliotheque() {
        // Arrange
        Long id = 1L;

        // Act
        bibliothequeService.deleteBibliotheque(id);

        // Assert
        Mockito.verify(bibliothequeDao, Mockito.times(1)).deleteById(id);
    }
}

