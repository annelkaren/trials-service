package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"domicilio"})
    Optional<Persona> findByIdAndEstadoIn(Long id, List<Estado> estados);

     @Query("""
                SELECT
                    new mx.gob.pjpuebla.trials.core.personas.PersonaRecord(
                        p.id,
                        p.nombre || " " || p.apellidoPaterno || " " ||  p.apellidoMaterno,
                        "",
                        "",
                        p.pseudonimo)
                FROM Persona p
            """)
        List<PersonaRecord> findAllJueces();
}