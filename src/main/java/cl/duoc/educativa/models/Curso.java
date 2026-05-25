package cl.duoc.educativa.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CURSOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cursos")
    @SequenceGenerator(name = "seq_cursos", sequenceName = "SEQ_CURSOS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "El instructor es obligatorio")
    @Column(name = "INSTRUCTOR", nullable = false, length = 150)
    private String instructor;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser positiva")
    @Column(name = "DURACION_HORAS", nullable = false)
    private Integer duracionHoras;

    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser positivo")
    @Column(name = "COSTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "CATEGORIA", length = 100)
    private String categoria;

    @Column(name = "DISPONIBLE", nullable = false)
    private Boolean disponible = true;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.disponible == null) this.disponible = true;
    }
}
