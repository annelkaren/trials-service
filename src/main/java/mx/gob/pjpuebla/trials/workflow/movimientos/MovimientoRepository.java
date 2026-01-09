package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.util.List;
import java.util.UUID;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer>, JpaSpecificationExecutor<Movimiento> {

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoSalidaRecord (
                m.uuid,
                c.tipoCarpeta,
                c.folio,
                c.expediente,
                m.fechaAsignacion,
                coalesce(j.nombre, jc.nombre),
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
                        LEFT JOIN Juzgado jc on jc = c.juzgado
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
                        ((m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                        (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                        AND (m2.estado != 'TURNADO' OR (m2.estado = 'TURNADO' AND m2.destino = :personaId))
                     )
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
                    OR (
                    (:tipoCarpeta IS NOT NULL AND COALESCE(c.folio, d.folio) = :folio AND c.tipoCarpeta = :tipoCarpeta)
                         OR (:tipoDocumento IS NOT NULL AND COALESCE(c.folio, d.folio) = :folio AND d.tipoDocumento = :tipoDocumento))
                )
                AND (
                (:tipoEntradaDoc IS NULL AND :tipoEntradaCarp IS NULL)
                OR (d IS NOT NULL AND :tipoEntradaDoc IS NOT NULL AND d.tipoDocumento = :tipoEntradaDoc)
                OR (d IS NULL AND cd IS NOT NULL AND :tipoEntradaCarp IS NOT NULL AND cd.tipoCarpeta = :tipoEntradaCarp)
                OR (d IS NULL AND cd IS NULL AND :tipoEntradaCarp IS NOT NULL AND c.tipoCarpeta = :tipoEntradaCarp)
            )
                order by m.fechaAsignacion desc
            """)
    Page<Movimiento> getAllBandejaRecepcion(Pageable pageable, Integer juzgadoId, List<EstadoCarpeta> estado,
            String key, List<String> motivos, Persona personaId, TipoCarpeta tipoCarpeta,
            TipoDocumento tipoDocumento, Integer folio, TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp);

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
                ORDER BY m.fechaAsignacion DESC
            """)
    Page<Movimiento> getBandejaRecepcion(Pageable pageable, Integer juzgadoId, EstadoCarpeta estado, String key,
            String motivos, Persona personaId);

    @Query(value = """
                                    SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord(


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN cd.id
                                        WHEN c IS NOT NULL THEN c.id
                                        ELSE cd.id
                                      END,


                                      CASE WHEN d IS NOT NULL THEN d.id ELSE NULL END,


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN d.folio
                                        WHEN c IS NOT NULL THEN c.folio
                                        ELSE cd.folio
                                      END,


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN cd.expediente
                                        WHEN c IS NOT NULL THEN c.expediente
                                        ELSE cd.expediente
                                      END,


                        CASE
                          WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN 'PROMOCION'
                          WHEN c IS NOT NULL THEN
                            CASE c.tipoCarpeta
                              WHEN 0 THEN 'DEMANDA'
                              WHEN 1 THEN 'EXHORTO'
                              WHEN 2 THEN 'APELACION'
                              WHEN 3 THEN 'DESPACHO'
                              WHEN 4 THEN 'APELACION_MUNICIPAL'
                              WHEN 5 THEN 'AMPARO'
                              WHEN 6 THEN 'CARTA_ROGATORIA'
                              WHEN 7 THEN 'COOPERACION_JUDICIAL_E_INTERNACIONAL'
                              WHEN 8 THEN 'OFICIO'
                              WHEN 9 THEN 'PIEZA'
                              ELSE ''
                            END
                          ELSE
                            CASE cd.tipoCarpeta
                              WHEN 0 THEN 'DEMANDA'
                              WHEN 1 THEN 'EXHORTO'
                              WHEN 2 THEN 'APELACION'
                              WHEN 3 THEN 'DESPACHO'
                              WHEN 4 THEN 'APELACION_MUNICIPAL'
                              WHEN 5 THEN 'AMPARO'
                              WHEN 6 THEN 'CARTA_ROGATORIA'
                              WHEN 7 THEN 'COOPERACION_JUDICIAL_E_INTERNACIONAL'
                              WHEN 8 THEN 'OFICIO'
                              WHEN 9 THEN 'PIEZA'
                              ELSE ''
                            END
                        END,



                                      concat(p.nombre,' ',p.apellidoPaterno,' ',coalesce(p.apellidoMaterno,'')),


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.nombre
                                        WHEN c IS NOT NULL THEN cc.nombre
                                        ELSE cdc.nombre
                                      END,


                                      m.fechaAsignacion,


                                      true,


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN cd.prioridad
                                        WHEN c IS NOT NULL THEN c.prioridad
                                        ELSE cd.prioridad
                                      END,


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN cd.horas
                                        WHEN c IS NOT NULL THEN c.horas
                                        ELSE cd.horas
                                      END,


                                      CASE
                                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.id
                                        WHEN c IS NOT NULL THEN cc.id
                                        ELSE cdc.id
                                      END,


                                      CASE
              WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                THEN COALESCE(concat(function('jsonb_extract_path_text', d.data, 'tipoPromocion'), ''), '')
              ELSE ''
            END

                                    )
                                    FROM Movimiento m
                                    LEFT JOIN m.carpeta c
                                    LEFT JOIN c.juzgado jc
                                    LEFT JOIN c.concepto cc

                                    LEFT JOIN m.documento d
                                    LEFT JOIN d.concepto dc
                                    LEFT JOIN d.carpeta cd
                                    LEFT JOIN cd.juzgado jcd
                                    LEFT JOIN cd.concepto cdc

                                    JOIN m.persona p
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
                                            (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                                         OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
                                        )
                                    )
                                    AND m.estado = :motivos
                                    AND m.destino = :personaId
                                    AND (
                                        (c IS NOT NULL AND jc.id = :juzgadoId)
                                     OR (d IS NOT NULL AND jcd.id = :juzgadoId)
                                    )
                                    AND (
                                        :key IS NULL OR :key = '' OR
                                        LOWER(COALESCE(c.folio, cd.folio, d.folio, '')) LIKE CONCAT('%', LOWER(:key), '%')
                                     OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(:key), '%')
                                     OR LOWER(p.nombre) LIKE CONCAT('%', LOWER(:key), '%')
                                     OR LOWER(p.apellidoPaterno) LIKE CONCAT('%', LOWER(:key), '%')
                                     OR LOWER(COALESCE(j.nombre, o.nombre, jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(:key), '%')
                                    )
                                    """, countQuery = """
            SELECT COUNT(m)
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN m.documento d
            LEFT JOIN d.carpeta cd
            LEFT JOIN cd.juzgado jcd
            JOIN m.persona p
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
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                 OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
                )
            )
            AND m.estado = :motivos
            AND m.destino = :personaId
            AND (
                (c IS NOT NULL AND jc.id = :juzgadoId)
             OR (d IS NOT NULL AND jcd.id = :juzgadoId)
            )
            AND (
                :key IS NULL OR :key = '' OR
                LOWER(COALESCE(c.folio, cd.folio, d.folio, '')) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(p.nombre) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(p.apellidoPaterno) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(COALESCE(j.nombre, o.nombre, jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(:key), '%')
            )
            """)
    Page<DocumentoBandejaRecepcionRecord> getBandejaRecepcionPro(
            Pageable pageable,
            Integer juzgadoId,
            EstadoCarpeta estado,
            String key,
            String motivos,
            Persona personaId);

    @Query("""
            SELECT COUNT(m)
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN m.documento d
            LEFT JOIN d.carpeta cd
            LEFT JOIN cd.juzgado jcd
            JOIN m.persona p
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
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                 OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
                )
            )
            AND m.estado = :motivos
            AND m.destino = :personaId
            AND (
                (c IS NOT NULL AND jc.id = :juzgadoId)
             OR (d IS NOT NULL AND jcd.id = :juzgadoId)
            )
            AND (
                :key IS NULL OR :key = '' OR
                LOWER(COALESCE(c.folio, cd.folio, d.folio, '')) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(p.nombre) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(p.apellidoPaterno) LIKE CONCAT('%', LOWER(:key), '%')
             OR LOWER(COALESCE(j.nombre, o.nombre, jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(:key), '%')
            )
            """)
    long countBandejaRecepcionWhereOnly(
            Integer juzgadoId,
            EstadoCarpeta estado,
            String key,
            String motivos,
            Persona personaId);

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
                    (c IS NOT NULL AND c.estatus IN (0,14))
                    OR (d IS NOT NULL AND d.estatus IN (0,14))
                )
                 AND m.fechaAsignacion = (
                    SELECT MAX(m2.fechaAsignacion)
                    FROM Movimiento m2
                    WHERE (
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                    (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                )
                AND m.estado IN ('CAPTURA','EDICION','DEVUELTO_A_OFICIALIA')
                AND ( o.id = :oficialiaId OR j.id = :juzgadoId )
                AND (
                    LOWER(c.folio) LIKE %:key%
                    OR LOWER(d.folio) LIKE %:key%
                    OR LOWER(cd.folio) LIKE %:key% OR LOWER(cd.expediente) LIKE %:key%
                    OR LOWER(c.expediente) LIKE %:key%
                    OR LOWER(c.juzgado.nombre) LIKE %:key%
                    OR (
                    (:tipoCarpeta IS NOT NULL AND COALESCE(c.folio, d.folio) = :folio AND c.tipoCarpeta = :tipoCarpeta)
                         OR (:tipoDocumento IS NOT NULL AND COALESCE(c.folio, d.folio) = :folio AND d.tipoDocumento = :tipoDocumento))
                )
                      AND (
                (:tipoEntradaDoc IS NULL AND :tipoEntradaCarp IS NULL)
                OR (d IS NOT NULL AND :tipoEntradaDoc IS NOT NULL AND d.tipoDocumento = :tipoEntradaDoc)
                OR (d IS NULL AND cd IS NOT NULL AND :tipoEntradaCarp IS NOT NULL AND cd.tipoCarpeta = :tipoEntradaCarp)
                OR (d IS NULL AND cd IS NULL AND :tipoEntradaCarp IS NOT NULL AND c.tipoCarpeta = :tipoEntradaCarp)
            )
            """)
    Page<Movimiento> getAllBandejaEntrada(Integer juzgadoId, Integer oficialiaId, String key, Pageable pageable,
            TipoCarpeta tipoCarpeta, TipoDocumento tipoDocumento, Integer folio, TipoDocumento tipoEntradaDoc,
            TipoCarpeta tipoEntradaCarp);

    Movimiento findFirstByCarpetaIdOrderByIdAsc(Integer documentoId);

    Movimiento findFirstByDocumentoIdOrderByIdAsc(Integer carpetaId);

    Movimiento findFirstByCarpetaIdOrderByIdDesc(Integer documentoId);

    Movimiento findFirstByDocumentoIdOrderByIdDesc(Integer carpetaId);

    List<Movimiento> findByCarpetaIdAndEstadoInOrderByIdAsc(Integer carpetaId, List<String> estados);

    Integer countByCarpetaId(Integer carpetaId);

    List<Movimiento> findByCarpetaIdOrderByFechaAsignacionDesc(Integer carpetaId);

    List<Movimiento> findByUuid(UUID uuid);

    Movimiento findTopByCarpetaIdOrderByFechaAsignacionDesc(Integer carpetaId);

    @Query("""
                select new mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord(
                    concat(
                        coalesce(persona.nombre, ''), ' ',
                        coalesce(persona.apellidoPaterno, ''), ' ',
                        coalesce(persona.apellidoMaterno, '')
                    ),
                    movimiento.cargo,
                    movimiento.fechaAsignacion
                )
                from Movimiento movimiento
                join movimiento.persona persona
                where movimiento.documento.id = :documentoId
                  and movimiento.estado = 'ASIGNADO'
                order by movimiento.id asc
            """)
    List<AcusePromocionDetailRecord> findAllAcusePromocionDetails(
            @Param("documentoId") Integer documentoId);

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
                AND (
                    (c IS NOT NULL AND jc in :juzgados)
                    OR (d IS NOT NULL AND jcd in :juzgados)
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
    Page<Movimiento> getBandejaDevueltos(Pageable pageable, List<Juzgado> juzgados, EstadoCarpeta estado, String key,
            String motivos);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse(
              m.id,
              doc.id,
              COALESCE(cDoc.id, cMov.id),
              COALESCE(cDoc.folio, cMov.folio),
              COALESCE(cDoc.expediente, cMov.expediente),
              COALESCE(matDoc.nombre, matMov.nombre),
              doc.tipoDocumento,
              COALESCE(cDoc.tipoCarpeta, cMov.tipoCarpeta),
              COALESCE(jcDoc.nombre, jcMov.nombre),
              m.fechaAsignacion,
              COALESCE(cDoc.selloEstatus, cMov.selloEstatus),
              COALESCE(cDoc.estatus, cMov.estatus),
              CASE
                WHEN doc IS NOT NULL THEN (doc.ruta IS NOT NULL)
                ELSE EXISTS (
                  SELECT 1 FROM Documento d
                  WHERE d.carpeta = cMov
                    AND d.tipoDocumento IS NULL
                    AND d.ruta IS NOT NULL
                )
              END,
              CASE
                WHEN m.estado = 'CAPTURA' THEN 'En Juzgado'
                WHEN m.estado = 'SALIDA' THEN 'En Juzgado'
                WHEN m.estado = 'DEVUELTO_A_OFICIALIA' THEN 'En Juzgado'
                ELSE ''
              END,
              m.motivo
            )
            FROM Movimiento m
            LEFT JOIN m.documento doc
            LEFT JOIN m.carpeta cMov
            LEFT JOIN doc.carpeta cDoc
            LEFT JOIN cMov.juzgado jcMov
            LEFT JOIN cDoc.juzgado jcDoc
            LEFT JOIN jcMov.materia matMov
            LEFT JOIN jcDoc.materia matDoc
            WHERE m.estado IN :estados
            AND (
              (
                doc IS NULL
                AND m.fechaAsignacion = (
                  SELECT MAX(m2.fechaAsignacion)
                  FROM Movimiento m2
                  WHERE m2.carpeta = cMov
                    AND m2.documento IS NULL
                    AND m2.estado IN :estados
                )
                AND m.id = (
                  SELECT MAX(m2b.id)
                  FROM Movimiento m2b
                  WHERE m2b.carpeta = cMov
                    AND m2b.documento IS NULL
                    AND m2b.estado IN :estados
                    AND m2b.fechaAsignacion = m.fechaAsignacion
                )
              )
              OR
              (
                doc IS NOT NULL
                AND m.fechaAsignacion = (
                  SELECT MAX(m3.fechaAsignacion)
                  FROM Movimiento m3
                  WHERE m3.documento = doc
                    AND m3.estado IN :estados
                )
                AND m.id = (
                  SELECT MAX(m3b.id)
                  FROM Movimiento m3b
                  WHERE m3b.documento = doc
                    AND m3b.estado IN :estados
                    AND m3b.fechaAsignacion = m.fechaAsignacion
                )
              )
            )
            """)
    Page<BandejaEntradaResponse> getBandejaEntradas(
            Pageable pageable,
            @Param("estados") List<String> estados);

    @Override
    @EntityGraph(attributePaths = {
            "carpeta", "carpeta.juzgado", "carpeta.juzgado.materia",
            "documento", "documento.carpeta", "documento.carpeta.juzgado", "documento.carpeta.juzgado.materia"
    })
    @NonNull
    Page<Movimiento> findAll(@Nullable Specification<Movimiento> spec, @Nullable Pageable pageable);

}
