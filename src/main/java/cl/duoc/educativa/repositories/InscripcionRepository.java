package cl.duoc.educativa.repositories;

import cl.duoc.educativa.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByEstudianteId(Long estudianteId);

    List<Inscripcion> findByEstudianteEmailIgnoreCase(String email);
}
