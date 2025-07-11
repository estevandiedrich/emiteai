package com.emiteai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("disabled") // Desabilitado - usando configuração no AuditoriaConfig
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",    // Para desenvolvimento local
                    "http://127.0.0.1:3000",   // Para desenvolvimento local
                    "http://localhost:80",      // Para Docker local
                    "http://127.0.0.1:80",     // Para Docker local
                    "http://frontend:80",       // Para comunicação entre containers
                    "http://localhost"          // Para casos sem porta específica
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
