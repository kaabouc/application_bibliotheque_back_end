//package com.Biblio.cours.web;
//
//import com.Biblio.cours.entities.Contact;
//import com.Biblio.cours.services.ContactService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/contacts")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS})
//@AllArgsConstructor
//public class ContactController {
//    private final ContactService contactService;
//
//    @PostMapping
//    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
//        return ResponseEntity.ok(contactService.saveContact(contact));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Contact>> getAllContacts() {
//        return ResponseEntity.ok(contactService.getAllContacts());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
//        contactService.deleteContact(id);
//        return ResponseEntity.ok().build();
//    }
//}