package com.Biblio.cours.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permet tous les endpoints
                .allowedOrigins("https://application-bibliotheque-front.onrender.com") // Domaine du frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes permises
                .allowedHeaders("*") // Autoriser tous les headers
                .allowCredentials(true); // Permettre l'utilisation de cookies ou d'authentification
    }
}
