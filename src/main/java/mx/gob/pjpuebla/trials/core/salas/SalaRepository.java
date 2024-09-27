package mx.gob.pjpuebla.trials.core.salas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.Estado;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.salas.SalaRecordResponse(s.id, s.nombre, s.estado, s.version,
                new mx.gob.pjpuebla.trials.core.personas.JuezRecord(juez.id, juez.nombre ||  juez.apellidoPaterno || juez.apellidoMaterno),
                new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(juzgado.id, juzgado.nombre, juzgado.estado, ""),
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal)
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
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal),
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

    @Query("""
             SELECT
                new mx.gob.pjpuebla.trials.core.salas.SalaRecord(
                s.id,
                s.nombre,
                j.nombre || " " || j.apellidoPaterno || " " || j.apellidoMaterno,
                '',
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal),
                s.estado
            )
            FROM Sala s
            LEFT JOIN s.juez j
            LEFT JOIN s.bloque b 
            WHERE s.estado = mx.pjpuebla.trials.util.enums.Estado.ACTIVE and s.juzgado=:juzgado
            AND NOT EXISTS
                (SELECT a from Audiencia a WHERE a.sala=s and a.bloque=s.bloque and a.fechaAudiencia=:fechaAudiencia)
            ORDER BY b, j
            """)
    List<SalaRecord> findSalaDisponible(LocalDateTime fechaAudiencia, Juzgado juzgado);

    @Query("""
             SELECT
                new mx.gob.pjpuebla.trials.core.salas.SalaRecord(
                s.id,
                s.nombre,
                j.nombre || " " || j.apellidoPaterno || " " || j.apellidoMaterno,
                '',
                new mx.gob.pjpuebla.trials.core.bloques.BloqueRecord(bloque.id, bloque.horaInicial, bloque.horaFinal),
                s.estado
            )
            FROM Sala s
            LEFT JOIN s.juez j
            LEFT JOIN s.bloque b
            WHERE s.estado = mx.pjpuebla.trials.util.enums.Estado.ACTIVE and s = :sala 
            and :fechaAudiencia between b.fechaInicial and b.fechaFinal
            AND NOT EXISTS
                (SELECT a from Audiencia a WHERE a.sala=s and a.bloque=s.bloque and a.fechaAudiencia=:fechaAudiencia)
            ORDER BY b
            """)
    List<SalaRecord> findHoraDisponible(LocalDateTime fechaAudiencia, Sala sala);

}
