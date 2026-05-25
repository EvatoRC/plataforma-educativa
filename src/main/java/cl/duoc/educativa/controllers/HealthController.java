package cl.duoc.educativa.controllers;

import cl.duoc.educativa.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> info = Map.of(
                "app", "Plataforma Educativa - Sistema de Inscripción",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString(),
                "status", "UP"
        );
        return ResponseEntity.ok(ApiResponse.ok("Servicio disponible", info));
    }
}
