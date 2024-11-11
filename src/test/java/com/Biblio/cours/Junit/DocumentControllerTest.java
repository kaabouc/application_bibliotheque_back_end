//package com.Biblio.cours.Junit;
//
//import com.Biblio.cours.entities.Document;
//import com.Biblio.cours.security.CustomUserDetailsService;
//import com.Biblio.cours.services.IDocumentService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest
//public class DocumentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private IDocumentService documentService;
//
//    // Mock CustomUserDetailsService to avoid security errors
//    @MockBean
//    private CustomUserDetailsService userDetailsService;
//
//    @Test
//    public void getDocumentById_ShouldReturnDocument_WithToken() throws Exception {
//        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2hhbWVkQGdtYWlsLmNvbSIsInR5cGUiOiJBRE1JTiIsImV4cCI6MTgxNjE2NjYxMywiaWF0IjoxNzI5NzY2NjEzfQ.YO9TG96Z82HJzK98U7xiCvXJPSLHGSrBLUnilUrPGWY";
//
//        Document mockDocument = new Document();
//        mockDocument.setId(1L);
//        mockDocument.setTitre("Test Document");
//
//        // Mock the service layer
//        Mockito.when(documentService.getDocumentById(1L)).thenReturn(Optional.of(mockDocument));
//
//        // Send GET request with Authorization header
//        mockMvc.perform(get("/api/document/8")
//                        .header("Authorization", token)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void getDocumentById_NotFound() throws Exception {
//        // Mock the service layer returning an empty Optional
//        Mockito.when(documentService.getDocumentById(1L)).thenReturn(Optional.empty());
//
//        // Send GET request and verify the 404 status
//        mockMvc.perform(get("/api/document/1"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void deleteDocument_ShouldReturnNoContent() throws Exception {
//        // Mock the service layer
//        Mockito.doNothing().when(documentService).deleteDocument(anyLong());
//
//        // Send DELETE request and verify the response
//        mockMvc.perform(delete("/api/document/delete/1"))
//
//                .andExpect(status().isNoContent());
//    }
//    @Test
//    public void updateDocument_ShouldReturnUpdatedDocument() throws Exception {
//        Document mockDocument = new Document();
//        mockDocument.setId(1L);
//        mockDocument.setTitre("Updated Title");
//
//        // Mock the service layer
//        Mockito.when(documentService.getDocumentById(1L)).thenReturn(Optional.of(mockDocument));
//        Mockito.when(documentService.saveDocument(Mockito.any(), Mockito.any())).thenReturn(mockDocument);
//
//        // Send PUT request and verify the response
//        mockMvc.perform(put("/api/document/1")
//                        .param("titre", "Updated Title")
//                        .param("description", "Updated Description")
//                        .param("filier", "Updated Filier")
//                        .param("niveaux", "Updated Niveaux")
//                        .param("bibliothequeId", "1")
//                        .param("typeId", "1")
//                        .param("userId", "1")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isOk());
//    }
//    @Test
//    public void getDocumentsByUser_ShouldReturnDocumentsList() throws Exception {
//        // Mocking a document list
//        List<Document> mockDocuments = Arrays.asList(new Document(1L, "Document1"), new Document(2L, "Document2"));
//
//        // Mock the service layer
//        Mockito.when(documentService.getDocumentsByUserId(1L)).thenReturn(mockDocuments);
//
//        // Send GET request and verify the response
//        mockMvc.perform(get("/api/document/user/1"))
//                .andExpect(status().isOk());
//    }
//
//}
