package services;



import com.Biblio.cours.dao.TypeDAO;
import com.Biblio.cours.entities.Type;
import com.Biblio.cours.services.TypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TypeServiceImplTest {

    @Mock
    private TypeDAO typeDAO;

    @InjectMocks
    private TypeServiceImpl typeService;

    private Type type;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        type = new Type();
        type.setId(1L);
        type.setName("Fiction");
        type.setSubtitle("Stories and Novels");
        type.setDescription("Fictional books");
    }

    @Test
    public void testSaveType() {
        when(typeDAO.save(type)).thenReturn(type);

        Type savedType = typeService.saveType(type);

        assertNotNull(savedType);
        assertEquals("Fiction", savedType.getName());
        verify(typeDAO, times(1)).save(type);
    }

    @Test
    public void testEditType() {
        Type updatedType = new Type();
        updatedType.setName("Updated Fiction");
        updatedType.setSubtitle("Updated Subtitle");
        updatedType.setDescription("Updated Description");

        when(typeDAO.findById(1L)).thenReturn(Optional.of(type));
        when(typeDAO.save(type)).thenReturn(type);

        Type result = typeService.editType(1L, updatedType);

        assertNotNull(result);
        assertEquals("Updated Fiction", result.getName());
        assertEquals("Updated Subtitle", result.getSubtitle());
        assertEquals("Updated Description", result.getDescription());
        verify(typeDAO, times(1)).save(type);
    }

    @Test
    public void testGetAllTypes() {
        Type anotherType = new Type("Non-Fiction");
        when(typeDAO.findAll()).thenReturn(Arrays.asList(type, anotherType));

        List<Type> types = typeService.getAllTypes();

        assertEquals(2, types.size());
        verify(typeDAO, times(1)).findAll();
    }

    @Test
    public void testGetTypeById() {
        when(typeDAO.findById(1L)).thenReturn(Optional.of(type));

        Optional<Type> foundType = typeService.getTypeById(1L);

        assertTrue(foundType.isPresent());
        assertEquals("Fiction", foundType.get().getName());
        verify(typeDAO, times(1)).findById(1L);
    }

    @Test
    public void testDeleteType() {
        typeService.deleteType(1L);

        verify(typeDAO, times(1)).deleteById(1L);
    }
}

