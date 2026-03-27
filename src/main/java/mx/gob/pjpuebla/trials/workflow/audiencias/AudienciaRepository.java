package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AsistenciaPersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaAgendaRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaOralidadFamiliarRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaProgramadaRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import org.springframework.data.repository.query.Param;

public interface AudienciaRepository extends JpaRepository<Audiencia, Integer> {

    Optional<Audiencia> findByCarpeta(Carpeta carpeta);

    List<Audiencia> findByCarpeta_id(Integer carpetaId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaProgramadaRecord(
                true,
                a.fechaAudiencia,
                a.sala.nombre,
                a.tipoAudiencia.nombre,
                concat(
                    a.sala.juez.nombre, ' ', a.sala.juez.apellidoPaterno, ' ',
                    COALESCE(a.sala.juez.apellidoMaterno, '') )
            )
            FROM Audiencia a
            WHERE a.carpeta.id = :carpetaId
            AND  a.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
            AND a.fechaAudiencia >= CURRENT_TIMESTAMP
            """)
    List<AudienciaProgramadaRecord> findProgramadasByCarpetaId(@Param("carpetaId") Integer carpetaId);

    Optional<Audiencia> findFirstByCarpetaOrderByIdDesc(Carpeta carpeta);

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
             AND a.estado = 0
            AND (
                :key IS NULL
                OR lower(c.expediente) LIKE %:key%
                OR lower(a.tipoAudiencia.nombre) LIKE %:key%
                OR lower(s.nombre) LIKE %:key%
            )
            """)
    Page<Audiencia> findByJuzgado(@Param("juzgado") Juzgado juzgado, @Param("key") String key, Pageable pageable);

    // TODO evaluar si incluir asistenciaPersonaDocumento
    @Query("""
             SELECT new mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord(
             audiencia.id,
             tipoAudiencia.nombre,
             CASE
                 WHEN juez IS NOT NULL THEN concat(juez.nombre, ' ', juez.apellidoPaterno, ' ', COALESCE(juez.apellidoMaterno, ''))
                 ELSE 'Por asignar'
             END,
             carpeta.expediente,
             carpeta.id,
             sala.nombre,
             audiencia.fechaAudiencia,
             audiencia.estatusAudiencia,
             juzgadoSala.id,
             tipoJuicio.nombre,
             null,
             audiencia.inicio,
             audiencia.fin,
             tipoSistema.nombre,
             materia.nombre
             )
             FROM Audiencia audiencia
             JOIN audiencia.carpeta carpeta
             JOIN carpeta.tipoJuicio tipoJuicio
             JOIN tipoJuicio.materia materia
             JOIN tipoJuicio.tipoSistema tipoSistema
             JOIN audiencia.tipoAudiencia tipoAudiencia
             JOIN audiencia.sala sala
             JOIN sala.juzgado juzgadoSala
             LEFT JOIN sala.juez juez
             WHERE juzgadoSala = :juzgado
              AND audiencia.estado = 0
             AND (
                 COALESCE(:key, '') = ''
                 OR lower(carpeta.expediente) LIKE %:key%
                 OR lower(tipoAudiencia.nombre) LIKE %:key%
                 OR lower(sala.nombre) LIKE %:key%
                 OR lower(CASE
                    WHEN juez IS NOT NULL THEN concat(juez.nombre, ' ', juez.apellidoPaterno, ' ', COALESCE(juez.apellidoMaterno, ''))
                    ELSE 'Por asignar'
                 END) LIKE %:key%
             )
             AND (
                 COALESCE(:tipoAudiencia, '') = ''
                 OR lower(tipoAudiencia.nombre) LIKE CONCAT('%', :tipoAudiencia, '%')
             )
             AND (
                 COALESCE(:juez, '') = ''
                 OR lower(CASE
                    WHEN juez IS NOT NULL THEN concat(juez.nombre, ' ', juez.apellidoPaterno, ' ', COALESCE(juez.apellidoMaterno, ''))
                    ELSE 'Por asignar'
                 END) LIKE CONCAT('%', :juez, '%')
             )
             AND (
                 COALESCE(:numCarpeta, '') = ''
                 OR lower(carpeta.expediente) LIKE CONCAT('%', :numCarpeta, '%')
             )
             AND (
                 COALESCE(:lugar, '') = ''
                 OR lower(sala.nombre) LIKE CONCAT('%', :lugar, '%')
             )
             AND (
                 :estatus IS NULL
                 OR audiencia.estatusAudiencia = :estatus
             )
             AND audiencia.fechaAudiencia >= :fechaFrom
             AND audiencia.fechaAudiencia < :fechaTo
            AND ( (:esSecretario = false OR sala.id IN :salaIdsPermitidas) )
        """)
    Page<AudienciasGeneralesResponseRecord> findAudienciasGenerales(
            Pageable pageable,
            @Param("juzgado") Juzgado juzgado,
            @Param("key") String key,
            @Param("tipoAudiencia") String tipoAudiencia,
            @Param("juez") String juez,
            @Param("numCarpeta") String numCarpeta,
            @Param("lugar") String lugar,
            @Param("estatus") EstatusAudiencia estatus,
            @Param("fechaFrom") LocalDateTime fechaFrom,
            @Param("fechaTo") LocalDateTime fechaTo,
            @Param("salaIdsPermitidas") List<Integer> salaIdsPermitidas,
            @Param("esSecretario") boolean esSecretario);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciaAgendaRecord(
            a.inicio,
            a.fin,
            a.tipoAudiencia.nombre)
            FROM Audiencia a
            JOIN a.sala s
            WHERE s.id = :salaId AND CAST(a.fechaAudiencia AS date) >= :fechaAudiencia
            """)
    List<AudienciaAgendaRecord> findBySalaIdAndFechaAudiencia(Integer salaId, Date fechaAudiencia);

    @Query("""
                SELECT COUNT(a) > 0
                FROM Audiencia a
                WHERE a.sala.id = :salaId
                  AND (
                    (:inicio BETWEEN a.inicio AND a.fin)
                    OR (:fin BETWEEN a.inicio AND a.fin)
                    OR (a.inicio BETWEEN :inicio AND :fin)
                  )
            """)
    boolean existeConflicto(Long salaId, LocalDateTime inicio, LocalDateTime fin);

    Audiencia findFirstByEstatusAudienciaOrderByFechaAudienciaDesc(EstatusAudiencia estatusAudiencia);

    @Query("""
                SELECT COUNT(a)
                FROM Audiencia a
                JOIN a.carpeta ca
                JOIN ca.juzgado juz
                JOIN juz.materia ma
                WHERE ma.nombre IN (:materias) AND a.inicio BETWEEN :inicio AND :fin
            """)
    Integer findAllPenales(List<String> materias, LocalDateTime inicio, LocalDateTime fin);

    @Query("""
                SELECT new mx.gob.pjpuebla.trials.workflow.audiencias.record.AsistenciaPersonaDocumento(
                    pd.id,
                    pd.nombre,
                    pd.apellidoPaterno,
                    pd.apellidoMaterno,
                    CASE
                        WHEN pd.rol = 0 THEN 'PRINCIPAL'
                        WHEN pd.rol = 1 THEN 'SECUNDARIO'
                        ELSE 'DESCONOCIDO'
                    END,
                    tp.nombre,
                    aa.asistencia,
                    di.name,
                    aa.urlDocumento
                )
                FROM PersonaDocumento pd
                JOIN pd.carpeta c
                JOIN pd.tipoPartes tp
                LEFT JOIN AsistenciaAudiencia aa
                    ON aa.personaDocumento = pd
                   AND aa.audiencia.id = :audienciaId
                LEFT JOIN aa.documentoIdentificacion di
                WHERE c.id = :carpetaId
            """)
    List<AsistenciaPersonaDocumento> findParticipantesByAudienciaId(
            @Param("carpetaId") Integer carpetaId,
            @Param("audienciaId") Integer audienciaId);

}
