package cl.duoc.educativa.services;

import cl.duoc.educativa.dto.CursoRequest;
import cl.duoc.educativa.models.Curso;
import cl.duoc.educativa.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    /** Lista todos los cursos disponibles */
    @Transactional(readOnly = true)
    public List<Curso> listarDisponibles() {
        return cursoRepository.findByDisponibleTrue();
    }

    /** Lista todos los cursos (admin) */
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    /** Obtiene un curso por ID */
    @Transactional(readOnly = true)
    public Curso obtenerPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Curso no encontrado con ID: " + id));
    }

    /** Agrega un nuevo curso */
    @Transactional
    public Curso agregar(CursoRequest req) {
        Curso curso = new Curso();
        curso.setNombre(req.getNombre());
        curso.setInstructor(req.getInstructor());
        curso.setDuracionHoras(req.getDuracionHoras());
        curso.setCosto(req.getCosto());
        curso.setDescripcion(req.getDescripcion());
        curso.setCategoria(req.getCategoria());
        curso.setDisponible(true);
        return cursoRepository.save(curso);
    }

    /** Deshabilita un curso (baja lógica) */
    @Transactional
    public Curso deshabilitar(Long id) {
        Curso curso = obtenerPorId(id);
        curso.setDisponible(false);
        return cursoRepository.save(curso);
    }

    /** Actualiza datos de un curso */
    @Transactional
    public Curso actualizar(Long id, CursoRequest req) {
        Curso curso = obtenerPorId(id);
        curso.setNombre(req.getNombre());
        curso.setInstructor(req.getInstructor());
        curso.setDuracionHoras(req.getDuracionHoras());
        curso.setCosto(req.getCosto());
        curso.setDescripcion(req.getDescripcion());
        curso.setCategoria(req.getCategoria());
        return cursoRepository.save(curso);
    }

    /** Buscar cursos por categoría */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorCategoria(String categoria) {
        return cursoRepository.findByCategoriaIgnoreCaseAndDisponibleTrue(categoria);
    }

    /** Buscar cursos por nombre */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorNombre(String nombre) {
        return cursoRepository.findByNombreContainingIgnoreCaseAndDisponibleTrue(nombre);
    }
}
