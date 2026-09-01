package com.deliverytech.delivery_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Controller responsável pelos endpoints de monitoramento da aplicação
 * Demonstra o uso de recursos modernos do Java 21
 */
@RestController
public class HealthController {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   /**
     * Endpoint para verificar o status da aplicação
     * @return Map com informações de saúde da aplicação
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        // Usando Map.of() (Java 9+) para criar mapa imutável
        Map<String, String> healthInfo = Map.of(
            "status", "FUNCIONANDO !!!",
            "timestamp", LocalDateTime.now().format(FORMATTER),
            "service", "Delivery API AGOSTO",
            "javaVersion", System.getProperty("java.version"),
            "springBootVersion", getClass().getPackage().getImplementationVersion() != null
                ? getClass().getPackage().getImplementationVersion() : "4.11.x",
            "environment", "development"
        );

        return ResponseEntity.ok(healthInfo);
    }   
/**
     * Endpoint com informações detalhadas da aplicação
     * Demonstra o uso de Records (Java 14+)
     * @return AppInfo com dados da aplicação
     */
    @GetMapping("/info")
    public ResponseEntity<AppInfo> info() {
        AppInfo appInfo = new AppInfo(
            "Delivery Tech API",
            "1.0.0",
            "Anderson Klay Demoner ",
            System.getProperty("java.version"),
            "Spring Boot 4.11.x",
            LocalDateTime.now().format(FORMATTER),
            "Sistema de delivery moderno desenvolvido com as mais recentes tecnologias Java"
        );

        return ResponseEntity.ok(appInfo);
    }   
 /**
     * Record para demonstrar recurso do Java 14+ (disponível no JDK 21)
     * Records são classes imutáveis ideais para DTOs
     */
    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework,
        String timestamp,
        String description
    ) {
        // Construtor compacto para validação (opcional)
        public AppInfo {
            if (application == null || application.isBlank()) {
                throw new IllegalArgumentException("Application name cannot be null or blank");
            }
        }
    }
}
