package cl.duoc.educativa.config;

import cl.duoc.educativa.models.Curso;
import cl.duoc.educativa.repositories.CursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pre-carga algunos cursos de ejemplo solo si la tabla CURSOS está vacía.
 * Sirve para que la app tenga datos visibles al desplegarse por primera vez
 * y se pueda grabar el video de demostración sin tener que crear todo a mano.
 *
 * No corre en el perfil 'test' (que usa H2 con datos propios de las pruebas).
 */
@Configuration
@Profile("!test")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedCursos(CursoRepository cursoRepository) {
        return args -> {
            long total = cursoRepository.count();
            if (total > 0) {
                log.info("Tabla CURSOS ya tiene {} registros, se omite la pre-carga.", total);
                return;
            }

            log.info("Tabla CURSOS vacia: insertando cursos de ejemplo...");

            List<Curso> demo = List.of(
                    nuevoCurso("Introduccion a Java", "Carlos Valverde",
                            40, new BigDecimal("89990"),
                            "Curso base de programacion en Java orientado a backend.",
                            "Programacion"),
                    nuevoCurso("Spring Boot desde cero", "Alonso Castillo",
                            60, new BigDecimal("129990"),
                            "Construye microservicios REST con Spring Boot, JPA y validaciones.",
                            "Backend"),
                    nuevoCurso("Docker y contenedores", "Carlos Valverde",
                            30, new BigDecimal("79990"),
                            "Empaqueta tus aplicaciones, publica en Docker Hub y orquesta contenedores.",
                            "DevOps"),
                    nuevoCurso("CI/CD con GitHub Actions", "Alonso Castillo",
                            25, new BigDecimal("69990"),
                            "Automatiza build, test y despliegue continuo a AWS EC2.",
                            "DevOps"),
                    nuevoCurso("Oracle Autonomous Database para desarrolladores", "Carlos Valverde",
                            35, new BigDecimal("99990"),
                            "Conecta tus apps a Oracle Cloud, modela esquemas y consume con JPA.",
                            "Base de Datos")
            );

            cursoRepository.saveAll(demo);
            log.info("Pre-carga completada: {} cursos insertados.", demo.size());
        };
    }

    private static Curso nuevoCurso(String nombre, String instructor, int horas,
                                    BigDecimal costo, String descripcion, String categoria) {
        Curso c = new Curso();
        c.setNombre(nombre);
        c.setInstructor(instructor);
        c.setDuracionHoras(horas);
        c.setCosto(costo);
        c.setDescripcion(descripcion);
        c.setCategoria(categoria);
        c.setDisponible(true);
        return c;
    }
}
