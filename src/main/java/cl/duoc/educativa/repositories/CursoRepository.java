package cl.duoc.educativa.repositories;

import cl.duoc.educativa.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /** Solo cursos marcados como disponibles */
    List<Curso> findByDisponibleTrue();

    /** Buscar por categoría */
    List<Curso> findByCategoriaIgnoreCaseAndDisponibleTrue(String categoria);

    /** Buscar por nombre parcial */
    List<Curso> findByNombreContainingIgnoreCaseAndDisponibleTrue(String nombre);
}
