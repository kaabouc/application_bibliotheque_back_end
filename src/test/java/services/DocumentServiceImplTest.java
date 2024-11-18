package services;



import com.Biblio.cours.dao.DocumentDAO;
import com.Biblio.cours.dao.UtilisateurDAO;
import com.Biblio.cours.entities.Bibliotheque;
import com.Biblio.cours.entities.Document;
import com.Biblio.cours.entities.Type;
import com.Biblio.cours.services.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceImplTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private DocumentDAO documentDao;

    @Mock
    private UtilisateurDAO utilisateurDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private TypedQuery<Document> typedQuery;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        uploadDirectory = "uploads"; // Example upload directory for testing
    }

//    @Test
//    public void testSaveDocument_withFile() throws IOException {
//        // Setup test data
//        Document document = new Document("Test Title", "Test Description", "CS", "Graduate", new Bibliotheque(), new Type());
//        String fileName = UUID.randomUUID().toString() + "_testFile.txt";
//        when(mockFile.isEmpty()).thenReturn(false);
//        when(mockFile.getOriginalFilename()).thenReturn("testFile.txt");
//
//        // Mock file saving
//        File uploadDir = new File(uploadDirectory);
//        if (!uploadDir.exists()) uploadDir.mkdirs();
//        doNothing().when(mockFile).transferTo(any(File.class));
//
//        // Execute method and verify
//        Document savedDocument = documentService.saveDocument(document, mockFile);
//        assertNotNull(savedDocument);
//        assertTrue(savedDocument.getFilePath().contains(fileName));
//
//        verify(documentDao, times(1)).save(document);
//    }

    @Test
    public void testGetAllDocuments() {
        when(documentDao.findAll()).thenReturn(List.of(new Document(), new Document()));

        List<Document> documents = documentService.getAllDocuments();

        assertEquals(2, documents.size());
        verify(documentDao, times(1)).findAll();
    }

    @Test
    public void testGetDocumentById_found() {
        Document document = new Document(1L, "Test Document");
        when(documentDao.findById(1L)).thenReturn(Optional.of(document));

        Optional<Document> result = documentService.getDocumentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Document", result.get().getTitre());
    }

    @Test
    public void testGetDocumentById_notFound() {
        when(documentDao.findById(1L)).thenReturn(Optional.empty());

        Optional<Document> result = documentService.getDocumentById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteDocument_existingDocument() throws IOException {
        Document document = new Document();
        document.setFilePath(uploadDirectory + "/testFile.txt");

        when(documentDao.findById(1L)).thenReturn(Optional.of(document));
        doNothing().when(documentDao).deleteById(1L);

        documentService.deleteDocument(1L);

        verify(documentDao, times(1)).deleteById(1L);
        verify(documentDao, times(1)).findById(1L);
    }

    @Test
    public void testSearchDocuments() {
        // Mock query setup
        when(entityManager.createQuery(anyString(), eq(Document.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new Document(), new Document()));

        List<Document> results = documentService.searchDocuments("title", "desc", "filier", "level", 1L, 2L);

        assertEquals(2, results.size());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Document.class));
        verify(typedQuery, times(1)).getResultList();
    }
}

