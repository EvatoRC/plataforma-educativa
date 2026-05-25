package cl.duoc.educativa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/** DTO para inscribir un estudiante en uno o más cursos */
@Data
public class InscripcionRequest {

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    private String nombreEstudiante;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String emailEstudiante;

    private String telefonoEstudiante;

    @NotEmpty(message = "Debe seleccionar al menos un curso")
    private List<Long> cursoIds;
}
