package mx.gob.pjpuebla.trials.core.salas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.core.personas.Persona;



@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.salas.SalaRecordResponse(s.id, s.nombre, s.estado, s.version,
                new mx.gob.pjpuebla.trials.core.personas.JuezRecord(juez.id, COALESCE(juez.nombre, '') || ' ' || COALESCE(juez.apellidoPaterno, '') || ' ' ||COALESCE(juez.apellidoMaterno, '')),
                new mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem(juzgado.id, juzgado.nombre, juzgado.estado, juzgado.materia.nombre),
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
       WHERE s.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE AND j.id = :juzgadoId
       """)
List<SalaRecord> findByJuzgado(Integer juzgadoId);

    long countByJuzgadoId(int juzgadoId);

    List<Sala> findAllByJuezId(Long id);

    List<Sala> findByJuzgado(Juzgado juzgado);

    @Query("""
            SELECT s
            FROM Sala s
            WHERE s.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE and s.juzgado=:juzgado
            and s.bloque = :bloque
            AND NOT EXISTS
                (SELECT 1 from Audiencia a WHERE a.sala = s and a.bloque=s.bloque and a.fechaAudiencia=:fechaAudiencia
                and a.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE)
            ORDER BY s.bloque, s.juez
            """)
    List<Sala> findSalaDisponible(LocalDateTime fechaAudiencia, Bloque bloque, Juzgado juzgado);

    @Query("""
            SELECT s
            FROM Sala s
            WHERE s.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            and s = :sala
            AND NOT EXISTS
                (SELECT 1 from Audiencia a WHERE a.sala = s and a.bloque=s.bloque and a.fechaAudiencia=:fechaAudiencia
                and a.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE)
            """)
    Optional<Sala> checkHoraDisponible(LocalDateTime fechaAudiencia, Sala sala);

    List<Sala> findByJuzgadoAndNombreContainingIgnoreCase(Juzgado juzgado, String nombre);
    
    Sala findByJuez(Persona juez);
}
