package mx.gob.pjpuebla.trials.core.salas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.salas.SalaRecordResponse(s.id, s.nombre, s.estado, s.version,
                new mx.gob.pjpuebla.trials.core.personas.JuezRecord(juez.id, juez.nombre ||  juez.apellidoPaterno || juez.apellidoMaterno),
                new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(juzgado.id, juzgado.nombre, juzgado.estado, ""),
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal, bloque.estado)
            )
            FROM Sala s
            LEFT JOIN s.juez juez
            LEFT JOIN s.juzgado juzgado
            LEFT JOIN s.bloque bloque
            WHERE s.id = :id AND s.estado IN :estados
            """)
    Optional<SalaRecordResponse> findByIdAndEstadoIn(Integer id, List<Estado> estados);

    @Query("""
             SELECT
                new mx.gob.pjpuebla.trials.core.salas.SalaRecord(
                s.id,
                s.nombre,
                juez.nombre || " " || juez.apellidoPaterno || " " || juez.apellidoMaterno,
                j.nombre,
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal, bloque.estado),
                s.estado
            )
            FROM Sala s
            LEFT JOIN s.juez juez
            LEFT JOIN s.juzgado j
            LEFT JOIN s.bloque b
            WHERE s.estado IN :estados
            """)
    List<SalaRecord> findByAllEstado(List<Estado> estados);

    long countByJuzgadoId(int juzgadoId);

    List<Sala> findAllByJuezId(Long id);

}
