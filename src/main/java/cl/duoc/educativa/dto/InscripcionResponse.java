package cl.duoc.educativa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Resumen/boleta de inscripción devuelta al cliente */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionResponse {

    private Long inscripcionId;
    private String estado;
    private LocalDateTime fechaInscripcion;

    // Datos del estudiante
    private Long estudianteId;
    private String nombreEstudiante;
    private String emailEstudiante;

    // Detalle de cursos inscritos
    private List<CursoResumen> cursos;

    // Totales
    private int cantidadCursos;
    private BigDecimal totalPagar;
    private String mensaje;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoResumen {
        private Long id;
        private String nombre;
        private String instructor;
        private Integer duracionHoras;
        private BigDecimal costo;
    }
}
