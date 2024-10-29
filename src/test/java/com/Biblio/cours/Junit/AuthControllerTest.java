package com.Biblio.cours.Junit;

import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.services.IUtilisateurService;
import com.Biblio.cours.web.AuthController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {

    @Mock
    private IUtilisateurService utilisateurService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");

        when(utilisateurService.getUtilisateurByEmail(utilisateur.getEmail())).thenReturn(Optional.of(utilisateur));

        ResponseEntity<?> response = authController.registerUser(utilisateur);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Email is already in use!", response.getBody());
    }

    @Test
    public void testRegisterUser_Success() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("newuser@example.com");
        utilisateur.setPassword("password");

        when(utilisateurService.getUtilisateurByEmail(utilisateur.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(utilisateur.getPassword())).thenReturn("encodedPassword");
        when(utilisateurService.saveUtilisateur(utilisateur)).thenReturn(utilisateur);

        ResponseEntity<?> response = authController.registerUser(utilisateur);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(utilisateur, response.getBody());
    }
}
