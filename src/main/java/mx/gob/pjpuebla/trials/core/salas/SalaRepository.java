package mx.gob.pjpuebla.trials.core.salas;

import java.util.List;
import java.util.Optional;

import org.jboss.resteasy.annotations.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.Estado;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    // se omite relación con jeuz porque no la hay pero debe de acompletarse.
    
        @Query("""
                SELECT
                new mx.gob.pjpuebla.trials.core.salas.SalaRecord(s.id, s.version, s.nombre, s.juez, s.juzgado, s.bloque)
                FROM Sala s
                LEFT JOIN s.bloque
                LEFT JOIN s.juzgado
                WHERE f.id = :id AND f.estado IN :estados """;
    )
    Optional<Sala> findByIdAndEstadoIn(Integer id, List<Estado> estados);
}
