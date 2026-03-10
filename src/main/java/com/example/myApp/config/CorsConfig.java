package com.example.myApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 * Configures Cross-Origin Resource Sharing (CORS) for the React frontend
 * This allows the React application running on localhost:3000 to communicate with the Spring Boot backend
 * 
 * @Configuration annotation indicates that this class provides Spring configuration
 * @Bean annotation registers the returned object as a Spring bean
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * Configure CORS mappings for the application
     * This method sets up CORS configuration to allow requests from the React frontend
     * 
     * @param corsRegistry The CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600L);
    }
}
