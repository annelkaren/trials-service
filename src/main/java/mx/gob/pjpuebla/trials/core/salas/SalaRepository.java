package mx.gob.pjpuebla.trials.core.salas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.Estado;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    // se omite relación con jeuz porque no la hay pero debe de acompletarse.

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.salas.SalaRecord(
                s.id,
                s.nombre,
                juez.nombre || " " || juez.apellidoPaterno || " " || juez.apellidoMaterno,
                j.nombre, 
                b
            )
            FROM Sala s
            LEFT JOIN s.juez juez
            LEFT JOIN s.juzgado j
            LEFT JOIN s.bloque b
            WHERE s.id = :id AND s.estado IN :estados
            """)
    Optional<SalaRecord> findByIdAndEstadoIn(Integer id, List<Estado> estados);



    @Query("""
             SELECT new mx.gob.pjpuebla.trials.core.salas.SalaRecord(
                s.id,
                s.nombre,
                juez.nombre || " " || juez.apellidoPaterno || " " || juez.apellidoMaterno,
                juzgado.nombre, 
                b
            )
            FROM Sala s
            LEFT JOIN s.juez juez
            LEFT JOIN s.juzgado juzgado
            LEFT JOIN s.bloque b
            WHERE s.estado IN :estados
            """)
    List<SalaRecord> findByAllEstado(List<Estado> estados);



}
