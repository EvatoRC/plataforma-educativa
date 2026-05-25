package cl.duoc.educativa.services;

import cl.duoc.educativa.dto.InscripcionRequest;
import cl.duoc.educativa.dto.InscripcionResponse;
import cl.duoc.educativa.models.Curso;
import cl.duoc.educativa.models.Estudiante;
import cl.duoc.educativa.models.Inscripcion;
import cl.duoc.educativa.repositories.CursoRepository;
import cl.duoc.educativa.repositories.EstudianteRepository;
import cl.duoc.educativa.repositories.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                               CursoRepository cursoRepository,
                               EstudianteRepository estudianteRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoRepository = cursoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Registra una inscripción y retorna resumen/boleta.
     * Si el estudiante ya existe por email, se reutiliza.
     */
    @Transactional
    public InscripcionResponse inscribir(InscripcionRequest req) {

        // 1. Resolver o crear estudiante
        Estudiante estudiante = estudianteRepository
                .findByEmailIgnoreCase(req.getEmailEstudiante())
                .orElseGet(() -> {
                    Estudiante nuevo = new Estudiante();
                    nuevo.setNombre(req.getNombreEstudiante());
                    nuevo.setEmail(req.getEmailEstudiante().toLowerCase());
                    nuevo.setTelefono(req.getTelefonoEstudiante());
                    return estudianteRepository.save(nuevo);
                });

        // 2. Validar y obtener cursos
        if (req.getCursoIds() == null || req.getCursoIds().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un curso");
        }

        List<Curso> cursos = req.getCursoIds().stream()
                .map(id -> cursoRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Curso no encontrado con ID: " + id)))
                .collect(Collectors.toList());

        // Verificar que todos los cursos estén disponibles
        List<Curso> noDisponibles = cursos.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getDisponible()))
                .collect(Collectors.toList());
        if (!noDisponibles.isEmpty()) {
            String nombres = noDisponibles.stream().map(Curso::getNombre).collect(Collectors.joining(", "));
            throw new IllegalStateException("Los siguientes cursos no están disponibles: " + nombres);
        }

        // 3. Calcular total
        BigDecimal total = cursos.stream()
                .map(Curso::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Persistir inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setCursos(cursos);
        inscripcion.setTotalPagar(total);
        inscripcion.setEstado("CONFIRMADA");
        Inscripcion guardada = inscripcionRepository.save(inscripcion);

        // 5. Construir respuesta/boleta
        return buildResponse(guardada);
    }

    /** Consulta inscripciones de un estudiante por email */
    @Transactional(readOnly = true)
    public List<InscripcionResponse> consultarPorEmail(String email) {
        return inscripcionRepository.findByEstudianteEmailIgnoreCase(email)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    /** Consulta una inscripción por ID */
    @Transactional(readOnly = true)
    public InscripcionResponse consultarPorId(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Inscripción no encontrada con ID: " + id));
        return buildResponse(inscripcion);
    }

    /** Cancela una inscripción (cambia estado) */
    @Transactional
    public InscripcionResponse cancelar(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Inscripción no encontrada con ID: " + id));
        inscripcion.setEstado("CANCELADA");
        return buildResponse(inscripcionRepository.save(inscripcion));
    }

    private InscripcionResponse buildResponse(Inscripcion inscripcion) {

        List<InscripcionResponse.CursoResumen> cursosResumen = inscripcion.getCursos().stream()
                .map(c -> InscripcionResponse.CursoResumen.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .instructor(c.getInstructor())
                        .duracionHoras(c.getDuracionHoras())
                        .costo(c.getCosto())
                        .build())
                .collect(Collectors.toList());

        return InscripcionResponse.builder()
                .inscripcionId(inscripcion.getId())
                .estado(inscripcion.getEstado())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .estudianteId(inscripcion.getEstudiante().getId())
                .nombreEstudiante(inscripcion.getEstudiante().getNombre())
                .emailEstudiante(inscripcion.getEstudiante().getEmail())
                .cursos(cursosResumen)
                .cantidadCursos(cursosResumen.size())
                .totalPagar(inscripcion.getTotalPagar())
                .mensaje("Inscripción " + inscripcion.getEstado().toLowerCase()
                        + " exitosamente. Total a pagar: $" + inscripcion.getTotalPagar())
                .build();
    }
}
