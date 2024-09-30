package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public interface AudienciaRepository extends JpaRepository<Audiencia, Integer> {

    Optional<Audiencia> findByCarpeta(Carpeta carpeta);

    @Query("""
        SELECT max(a.fechaAudiencia) from Audiencia a
        WHERE a.tipoAudiencia = :tipoAudiencia 
        and a.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
        and EXISTS(
            Select 1 FROM Sala s where a.sala = s and s.juzgado = :juzgado
            and s.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
        )

    """)
    LocalDateTime getFechaUltimaAudiencia(Juzgado juzgado, TipoAudiencia tipoAudiencia);

}
