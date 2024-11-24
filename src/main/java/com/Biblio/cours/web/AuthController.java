package com.Biblio.cours.web;

import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.security.CustomUserDetailsService;
import com.Biblio.cours.security.JwtResponse;
import com.Biblio.cours.security.LoginRequest;
import com.Biblio.cours.services.IUtilisateurService;
import com.Biblio.cours.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin("https://e-read-me.onrender.com")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private IUtilisateurService utilisateurService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Utilisateur utilisateur) {
        // Check if user already exists
        if (utilisateurService.getUtilisateurByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Encode password
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));


        // Définir le type par défaut "CLIENT"
        String typeUtilisateur = utilisateur.getType();
        if (typeUtilisateur == null || typeUtilisateur.isEmpty()) {
            typeUtilisateur = "CLIENT";
        }
        utilisateur.setType(typeUtilisateur);

        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);

        return ResponseEntity.ok(savedUtilisateur);
    }


    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
    @PostMapping("/changeUserType")
    public ResponseEntity<?> changeUserType(@RequestParam String email, @RequestParam String newType) {
        Optional<Utilisateur> optionalUser = utilisateurService.getUtilisateurByEmail(email);
        if (optionalUser.isPresent()) {
            Utilisateur user = optionalUser.get();
            user.setType(newType.toUpperCase());
            utilisateurService.saveUtilisateur(user);

            // Generate a new token with updated type
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String newToken = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(newToken));
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}