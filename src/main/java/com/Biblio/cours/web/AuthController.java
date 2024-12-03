package com.Biblio.cours.web;

import com.Biblio.cours.entities.Utilisateur;
import com.Biblio.cours.security.CustomUserDetailsService;
import com.Biblio.cours.security.JwtResponse;
import com.Biblio.cours.security.LoginRequest;
import com.Biblio.cours.services.IUtilisateurService;
import com.Biblio.cours.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@CrossOrigin(origins ={"http://localhost:3000", "https://e-read-me.onrender.com"})
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
    public ResponseEntity<?> registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("image") MultipartFile image
    ) {
        // Check if user already exists
        if (utilisateurService.getUtilisateurByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Determine the destination directory path (where images will be stored)
        String pwd = System.getProperty("user.dir");
        System.out.println("Current directory: " + pwd);

        // Ensure the folder exists, create if necessary
        File directory = new File(pwd + "/temp");
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }

        // Create a file path and store the image in the destination folder
        String imagePath = "/temp/" + image.getOriginalFilename();
        File destination = new File(directory, image.getOriginalFilename());

        try {
            // Save the file to the destination folder
            image.transferTo(destination);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving the image.");
        }

        // Set image path in the utilisateur object
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setImagePath(pwd + imagePath);

        // Set default user type "CLIENT"
        if (utilisateur.getType() == null || utilisateur.getType().isEmpty()) {
            utilisateur.setType("CLIENT");
        }

        // Save the user
        Utilisateur savedUtilisateur = utilisateurService.saveUtilisateur(utilisateur);

        // Return the saved user
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