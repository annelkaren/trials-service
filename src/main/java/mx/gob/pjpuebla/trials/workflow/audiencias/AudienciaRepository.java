package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.util.Optional;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord(
                jue.nombre, jue.apellidoPaterno, jue.apellidoMaterno, s.nombre, tj.nombre, a.fechaAudiencia)
            FROM Audiencia a
            JOIN a.carpeta c
            JOIN c.tipoJuicio tj
            JOIN a.sala s
            JOIN s.juez jue
            WHERE c.id = :carpetaId
            """)
    AudienciaOralidadFamiliarRecord getJuzAndSalaAndAudienciaByIdcarpeta(@Param("carpetaId") Integer carpetaId);

    @Query("""
                SELECT s.nombre
                FROM Audiencia a
                JOIN a.sala s
                WHERE a.carpeta.id = :carpetaId
            """)
    String getSalaNombreByCarpetaId(@Param("carpetaId") Integer carpetaId);

    @Query("""
            SELECT a FROM Audiencia a
            JOIN a.carpeta c
            JOIN a.sala s
            WHERE s.juzgado = :juzgado
            AND (
                :key IS NULL
                OR lower(c.expediente) LIKE %:key%
                OR lower(a.tipoAudiencia.nombre) LIKE %:key%
                OR lower(s.nombre) LIKE %:key%
            )
            """)
    Page<Audiencia> findByJuzgado(@Param("juzgado") Juzgado juzgado, @Param("key") String key, Pageable pageable);
}
