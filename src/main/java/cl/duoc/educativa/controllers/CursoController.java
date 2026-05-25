package cl.duoc.educativa.controllers;

import cl.duoc.educativa.dto.ApiResponse;
import cl.duoc.educativa.dto.CursoRequest;
import cl.duoc.educativa.models.Curso;
import cl.duoc.educativa.services.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /**
     * GET /api/cursos
     * Lista todos los cursos disponibles.
     * Soporta filtros opcionales: ?categoria=X  o  ?nombre=X
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Curso>>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre) {

        List<Curso> cursos;
        if (categoria != null && !categoria.isBlank()) {
            cursos = cursoService.buscarPorCategoria(categoria);
        } else if (nombre != null && !nombre.isBlank()) {
            cursos = cursoService.buscarPorNombre(nombre);
        } else {
            cursos = cursoService.listarDisponibles();
        }

        return ResponseEntity.ok(ApiResponse.ok(
                "Se encontraron " + cursos.size() + " curso(s) disponible(s)", cursos));
    }

    /**
     * GET /api/cursos/{id}
     * Detalle de un curso específico.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Curso>> obtener(@PathVariable Long id) {
        try {
            Curso curso = cursoService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.ok("Curso encontrado", curso));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * POST /api/cursos
     * Agrega un nuevo curso a la oferta educativa.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Curso>> agregar(@Valid @RequestBody CursoRequest request) {
        Curso nuevo = cursoService.agregar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Curso agregado exitosamente", nuevo));
    }

    /**
     * PUT /api/cursos/{id}
     * Actualiza los datos de un curso existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Curso>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequest request) {
        try {
            Curso actualizado = cursoService.actualizar(id, request);
            return ResponseEntity.ok(ApiResponse.ok("Curso actualizado", actualizado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * DELETE /api/cursos/{id}
     * Deshabilita un curso (baja lógica, no borra de BD).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Curso>> deshabilitar(@PathVariable Long id) {
        try {
            Curso deshabilitado = cursoService.deshabilitar(id);
            return ResponseEntity.ok(ApiResponse.ok("Curso deshabilitado", deshabilitado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
