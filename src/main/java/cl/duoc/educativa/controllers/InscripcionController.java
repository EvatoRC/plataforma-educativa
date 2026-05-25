package cl.duoc.educativa.controllers;

import cl.duoc.educativa.dto.ApiResponse;
import cl.duoc.educativa.dto.InscripcionRequest;
import cl.duoc.educativa.dto.InscripcionResponse;
import cl.duoc.educativa.services.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    /**
     * POST /api/inscripciones
     * Inscribe a un estudiante en uno o más cursos.
     * Devuelve resumen/boleta con total a pagar.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InscripcionResponse>> inscribir(
            @Valid @RequestBody InscripcionRequest request) {
        try {
            InscripcionResponse boleta = inscripcionService.inscribir(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Inscripción realizada exitosamente", boleta));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/inscripciones/{id}
     * Consulta el detalle/boleta de una inscripción por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InscripcionResponse>> obtener(@PathVariable Long id) {
        try {
            InscripcionResponse boleta = inscripcionService.consultarPorId(id);
            return ResponseEntity.ok(ApiResponse.ok("Inscripción encontrada", boleta));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /api/inscripciones?email=X
     * Consulta todas las inscripciones de un estudiante por email.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InscripcionResponse>>> consultarPorEmail(
            @RequestParam String email) {
        List<InscripcionResponse> inscripciones = inscripcionService.consultarPorEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(
                "Se encontraron " + inscripciones.size() + " inscripción(es)", inscripciones));
    }

    /**
     * DELETE /api/inscripciones/{id}
     * Cancela una inscripción (cambia estado a CANCELADA).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<InscripcionResponse>> cancelar(@PathVariable Long id) {
        try {
            InscripcionResponse cancelada = inscripcionService.cancelar(id);
            return ResponseEntity.ok(ApiResponse.ok("Inscripción cancelada", cancelada));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
