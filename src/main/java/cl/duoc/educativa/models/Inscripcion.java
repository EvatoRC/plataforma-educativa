package cl.duoc.educativa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INSCRIPCIONES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inscripciones")
    @SequenceGenerator(name = "seq_inscripciones", sequenceName = "SEQ_INSCRIPCIONES", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ESTUDIANTE_ID", nullable = false)
    private Estudiante estudiante;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "INSCRIPCION_CURSOS",
        joinColumns = @JoinColumn(name = "INSCRIPCION_ID"),
        inverseJoinColumns = @JoinColumn(name = "CURSO_ID")
    )
    private List<Curso> cursos = new ArrayList<>();

    @Column(name = "TOTAL_PAGAR", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPagar;

    @Column(name = "ESTADO", nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(name = "FECHA_INSCRIPCION", nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    protected void onCreate() {
        this.fechaInscripcion = LocalDateTime.now();
        if (this.estado == null) this.estado = "PENDIENTE";
    }
}
