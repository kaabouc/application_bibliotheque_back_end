package services;

import com.Biblio.cours.dao.ContactRepository;
import com.Biblio.cours.entities.Contact;
import com.Biblio.cours.services.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ContactServiceImplTest {

    @InjectMocks
    private ContactServiceImpl contactService;  // The class being tested

    @Mock
    private ContactRepository contactRepository;  // Mocked repository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    void testGetAllContacts() {
        // Arrange
        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setFullName("John Doe");
        contact1.setEmail("john@example.com");
        contact1.setSubject("Subject 1");
        contact1.setMessage("Message 1");

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setFullName("Jane Doe");
        contact2.setEmail("jane@example.com");
        contact2.setSubject("Subject 2");
        contact2.setMessage("Message 2");

        List<Contact> contacts = Arrays.asList(contact1, contact2);

        when(contactRepository.findAll()).thenReturn(contacts);  // Mocking repository behavior

        // Act
        List<Contact> result = contactService.getAllContcats();

        // Assert
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        assertEquals("Subject 1", result.get(0).getSubject());
        verify(contactRepository, times(1)).findAll();  // Verify findAll() was called once
    }

    @Test
    void testSaveContact() {
        // Arrange
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFullName("John Doe");
        contact.setEmail("john@example.com");
        contact.setSubject("Subject");
        contact.setMessage("Message");

        when(contactRepository.save(contact)).thenReturn(contact);  // Mocking save behavior

        // Act
        Contact result = contactService.saveContcat(contact);

        // Assert
        assertEquals("John Doe", result.getFullName());
        assertEquals("Subject", result.getSubject());
        verify(contactRepository, times(1)).save(contact);  // Verify save() was called once
    }

    @Test
    void testDeleteContact() {
        // Arrange
        Long contactId = 1L;

        doNothing().when(contactRepository).deleteById(contactId);  // Mocking delete behavior

        // Act
        contactService.deleteContact(contactId);

        // Assert
        verify(contactRepository, times(1)).deleteById(contactId);  // Verify deleteById() was called once
    }
}
