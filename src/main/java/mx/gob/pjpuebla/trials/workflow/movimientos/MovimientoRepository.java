package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    @Query
            ("""
                    SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord (
                    m.uuid, c.tipoCarpeta, c.folio, c.expediente, m.fechaAsignacion, j.nombre, d.data,
                    d.folio, d.tipoDocumento, cd.expediente)
                    FROM Movimiento m
                    LEFT JOIN Carpeta c on c = m.carpeta and c.estatus = :estadoCarpeta
                    LEFT JOIN Documento d on d = m.documento and d.estatus = :estadoCarpeta
                    LEFT JOIN Juzgado j on j = m.juzgado
                    LEFT JOIN Carpeta cd on cd = d.carpeta
                    WHERE m.uuid = :uuid
                    ORDER BY j.id, c.tipoCarpeta, c.folio, d.folio
                    """)
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
    Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado, String key, List<String> motivos);

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
            (c IS NOT NULL AND c.estatus IN (0))
            OR (d IS NOT NULL AND d.estatus IN (0))
        )
        AND m.estado IN ('CAPTURA')
        AND ( o.id = :oficialiaId OR j.id = :juzgadoId )
        AND (
            LOWER(c.folio) LIKE %:key%
            OR LOWER(d.folio) LIKE %:key%
            OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
            OR LOWER(c.expediente) LIKE %:key%
        )
    """)
    Page<Movimiento> getAllBandejaEntrada(Integer juzgadoId, Integer oficialiaId, String key, Pageable pageable);

}
