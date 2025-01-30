package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;
import java.util.UUID;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord (
                m.uuid,
                c.tipoCarpeta,
                c.folio,
                c.expediente,
                m.fechaAsignacion,
                j.nombre,
                d.data,
                d.folio,
                d.tipoDocumento,
                cd.expediente,
                o.nombre,
                CONCAT(m.persona.nombre, ' ', m.persona.apellidoPaterno, ' ', COALESCE(m.persona.apellidoMaterno,'')) as responsable,
                m.observaciones,
                m.documento.id,
                m.carpeta.id
            )
                        FROM Movimiento m
                        LEFT JOIN Carpeta c on c = m.carpeta and c.estatus = :estadoCarpeta
                        LEFT JOIN Documento d on d = m.documento and d.estatus = :estadoCarpeta
                        LEFT JOIN Juzgado j on j = m.juzgado
                        LEFT JOIN Carpeta cd on cd = d.carpeta
                        LEFT JOIN Oficialia o on o = m.persona.oficialia
                        WHERE m.uuid = :uuid
                        ORDER BY j.id, c.tipoCarpeta, c.id, d.id""")
    List<MovimientoSalidaRecord> getSalidas(UUID uuid, EstadoCarpeta estadoCarpeta);

    @Query("""
                SELECT m
                FROM Movimiento m
                LEFT JOIN m.carpeta c
                LEFT JOIN c.juzgado jc
                LEFT JOIN m.documento d
                LEFT JOIN d.carpeta cd
                LEFT JOIN cd.juzgado jcd
                JOIN FETCH m.persona p
                LEFT JOIN m.juzgado j
                LEFT JOIN m.oficialia o
                WHERE (
                    (c IS NOT NULL AND c.estatus IN :estado)
                    OR (d IS NOT NULL AND d.estatus IN :estado)
                )
                AND m.fechaAsignacion = (
                    SELECT MAX(m2.fechaAsignacion)
                    FROM Movimiento m2
                    WHERE (
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                    (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                )
                AND m.estado IN (:motivos)
                AND (
                    (c IS NOT NULL AND jc.id = :juzgadoId)
                    OR (d IS NOT NULL AND jcd.id = :juzgadoId)
                )
                AND (
                    LOWER(c.folio) LIKE %:key%
                    OR LOWER(c.expediente) LIKE %:key%
                    OR LOWER(d.folio) LIKE %:key%
                    OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
                    OR LOWER(p.nombre) LIKE %:key% OR LOWER(p.apellidoPaterno) LIKE %:key%
                    OR LOWER(j.nombre) LIKE %:key% OR LOWER(o.nombre) LIKE %:key%
                )
            """)
    Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado,
            String key, List<String> motivos);

    @Query("""
                SELECT m
                FROM Movimiento m
                LEFT JOIN m.carpeta c
                LEFT JOIN c.juzgado jc
                LEFT JOIN m.documento d
                LEFT JOIN d.carpeta cd
                LEFT JOIN cd.juzgado jcd
                JOIN FETCH m.persona p
                LEFT JOIN m.juzgado j
                LEFT JOIN m.oficialia o
                WHERE (
                    (c IS NOT NULL AND c.estatus = :estado)
                    OR (d IS NOT NULL AND d.estatus = :estado)
                )
                AND m.fechaAsignacion = (
                    SELECT MAX(m2.fechaAsignacion)
                    FROM Movimiento m2
                    WHERE (
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                    (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                )
                AND m.estado = :motivos
                AND m.destino = :personaId
                AND (
                    (c IS NOT NULL AND jc.id = :juzgadoId)
                    OR (d IS NOT NULL AND jcd.id = :juzgadoId)
                )
                AND (
                    LOWER(c.folio) LIKE %:key%
                    OR LOWER(c.expediente) LIKE %:key%
                    OR LOWER(d.folio) LIKE %:key%
                    OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
                    OR LOWER(p.nombre) LIKE %:key% OR LOWER(p.apellidoPaterno) LIKE %:key%
                    OR LOWER(j.nombre) LIKE %:key% OR LOWER(o.nombre) LIKE %:key%
                )
            """)
    Page<Movimiento> getBandejaRecepcion(Pageable pageable, Integer juzgadoId, EstadoCarpeta estado, String key,
            String motivos, Persona personaId);

    @Query("""
                SELECT m
                FROM Movimiento m
                LEFT JOIN m.carpeta c
                LEFT JOIN c.juzgado jc
                LEFT JOIN jc.materia mat
                LEFT JOIN m.documento d
                LEFT JOIN d.carpeta cd
                LEFT JOIN cd.juzgado jcd
                LEFT JOIN jcd.materia matd
                LEFT JOIN m.juzgado j
                LEFT JOIN m.oficialia o
                WHERE (
                        (:oficialiaId IS null AND :juzgadoId IS null) OR
                        (:oficialiaId IS NOT null AND o.id = :oficialiaId) OR
                        (:juzgadoId IS NOT null AND j.id = :juzgadoId))
                AND (
                    LOWER(c.folio) LIKE %:key% OR LOWER(c.expediente) LIKE %:key%
                    OR LOWER(d.folio) LIKE %:key%
                    OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
                    OR LOWER(mat.nombre) LIKE %:key% OR LOWER(matd.nombre) LIKE %:key%
                )
            """)
    Page<Movimiento> getAllBandejaHistorial(String key, Integer juzgadoId, Integer oficialiaId, Pageable pageable);

    @Query("""
                SELECT m
                FROM Movimiento m
                LEFT JOIN m.carpeta c
                LEFT JOIN c.juzgado jc
                LEFT JOIN m.documento d
                LEFT JOIN d.carpeta cd
                LEFT JOIN cd.juzgado jcd
                LEFT JOIN m.juzgado j
                LEFT JOIN m.oficialia o
                WHERE (
                    (c IS NOT NULL AND c.estatus IN (0,12))
                    OR (d IS NOT NULL AND d.estatus IN (0,12))
                )
                 AND m.fechaAsignacion = (
                    SELECT MAX(m2.fechaAsignacion)
                    FROM Movimiento m2
                    WHERE (
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                    (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                )
                AND m.estado IN ('CAPTURA','EDICION')
                AND ( o.id = :oficialiaId OR j.id = :juzgadoId )
                AND (
                    LOWER(c.folio) LIKE %:key%
                    OR LOWER(d.folio) LIKE %:key%
                    OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
                    OR LOWER(c.expediente) LIKE %:key%
                    OR LOWER(c.juzgado.nombre) LIKE %:key%
                )
            """)
    Page<Movimiento> getAllBandejaEntrada(Integer juzgadoId, Integer oficialiaId, String key, Pageable pageable);

    Movimiento findFirstByCarpetaIdOrderByIdAsc(Integer documentoId);

    Movimiento findFirstByDocumentoIdOrderByIdAsc(Integer carpetaId);

    List<Movimiento> findByCarpetaIdAndEstadoInOrderByIdAsc(Integer carpetaId, List<String> estados);

    Integer countByCarpetaId(Integer carpetaId);

    List<Movimiento> findByUuid(UUID uuid);

    Movimiento findTopByCarpetaIdOrderByFechaAsignacionDesc(Integer carpetaId);
}
