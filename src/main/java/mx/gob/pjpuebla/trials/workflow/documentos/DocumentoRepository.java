package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.archivojudicial.ArchivoJudicialProjection;
import mx.gob.pjpuebla.trials.workflow.archivojudicial.RecibidosProjection;
import mx.gob.pjpuebla.trials.workflow.archivojudicial.SolicitudesProjection;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer>, SecuenciaRepositoryCustom {

        @Query(value = "SELECT doc FROM Documento doc "
                        + "JOIN FETCH doc.carpeta c "
                        + "JOIN FETCH c.juzgado j "
                        + "JOIN FETCH j.materia m "
                        + "WHERE c.estatus = mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.CAPTURA "
                        + "AND (lower(m.nombre) LIKE %:key% OR lower(c.folio) LIKE %:key% OR lower(c.expediente) LIKE %:key%) "
                        + "ORDER BY doc.audit.fechaAlta ASC")
        Page<Documento> findByEstatusCaptura(String key, Pageable pageable);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoJuzgadoRecord(
                            d.nombre,
                            j.nombre
                        )
                        FROM Documento doc
                        JOIN doc.carpeta c
                        JOIN c.juzgado j
                        JOIN j.sede s
                        JOIN s.distrito d
                        WHERE doc.id = :documentoId
                        """)
        DocumentoJuzgadoRecord findDistritoJuzgadoByDocumentoId(@Param("documentoId") Integer documentoId);

        Documento findByCarpetaIdAndTipoDocumentoIsNull(Integer id);

        Documento findByCarpetaIdAndTipoDocumento(Integer id, TipoDocumento tipoDocumento);

        Documento findByCarpetaIdAndRutaIsNull(Integer id);

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
                                             LEFT JOIN c.persona pc
                                             LEFT JOIN d.persona pd
                                             WHERE (
                                                 (c IS NOT NULL AND c.estatus IN (
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.TURNADO,
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.ASIGNADO,
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.DEVUELTO
                                                 ) AND pc = :personaAsignada AND jc.id = :juzgadoId)

                                                 OR case when :isOficial = true THEN (d IS NOT NULL AND d.estatus IN (
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.TURNADO,
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.ASIGNADO,
                                                 mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.DEVUELTO
                                                 ) AND pd = :personaAsignada
                                                 AND jsonb_extract_path_text(d.data, 'pieza') is null
                                                 AND jcd.id = :juzgadoId) else false  end > false
                                             )
                                              AND m.fechaAsignacion = (
                                                 SELECT MAX(m2.fechaAsignacion)
                                                 FROM Movimiento m2
                                                 WHERE (
                                                 (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                                                 (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id))
                                             )
                                             AND m.estado IN ('TURNADO','ASIGNADO','DEVUELTO')
                                             AND (
                                                 LOWER(c.folio) LIKE %:key%
                                                 OR LOWER(d.folio) LIKE %:key%
                                                 OR LOWER(cd.folio) LIKE %:key%
                                                 OR LOWER(cd.expediente) LIKE %:key%
                                                 OR LOWER(c.expediente) LIKE %:key%
                                                 OR CAST(m.uuid AS text) = :key
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
                        ORDER BY m.fechaAsignacion ASC
                        """)
        Page<Movimiento> findByPersonaAsignada(String key, Integer juzgadoId, Persona personaAsignada,
                        boolean isOficial,
                        Pageable pageable, TipoCarpeta tipoCarpeta, TipoDocumento tipoDocumento, Integer folio,
                        TipoDocumento tipoEntradaDoc, TipoCarpeta tipoEntradaCarp);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.OficioResponseRecord(
                            doc.id,
                            doc.folio,
                            ins.nombre,
                            dd.asunto,
                            doc.estatus,
                            '' as estatusEtiqueta,
                            dd.fechaEmision,
                            dd.fechaEntrega,
                            CASE WHEN dd.ruta IS NOT NULL THEN true ELSE false END,
                            CASE WHEN COUNT(dc) > 0 THEN true ELSE false END,
                            MAX(dc.tamanioPapel),
                            COALESCE(c.expediente, 'N/A')
                        )
                        FROM Documento doc
                        LEFT JOIN doc.institucion ins
                        LEFT JOIN DocumentoDetalle dd ON dd.documento = doc
                        LEFT JOIN DocumentoContenido dc ON dc.documento = doc
                        LEFT JOIN doc.carpeta c
                        WHERE doc.tipoDocumento = :tipoDocumento
                        AND CASE WHEN c IS NOT NULL THEN c.juzgado IN :juzgados ELSE true END
                        AND (
                            lower(doc.folio) LIKE %:key% OR
                            lower(ins.nombre) LIKE %:key% OR
                            lower(dd.asunto) LIKE %:key% OR
                            lower(COALESCE(c.expediente, 'N/A')) LIKE %:key%
                        )
                        GROUP BY doc.id, ins.nombre, dd.asunto, doc.estatus, dd.fechaEmision, dd.fechaEntrega, dd.ruta, c.expediente
                        """)
        Page<OficioResponseRecord> findAllByTipoDocumento(String key, TipoDocumento tipoDocumento, Pageable pageable,
                        List<Juzgado> juzgados);

        @Transactional
        @Modifying
        @Query("UPDATE Documento d SET d.estatus = :estado WHERE d.id = :documentoId")
        void actualizarEstatus(@Param("documentoId") Integer documentoId, @Param("estado") EstadoCarpeta estado);

        // colocamos id al acerdo momentaneamente ya que no se genera actualmente folio
        @Query("""
                            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord(
                                doc.id,
                                CASE
                                    WHEN doc.tipoDocumento = 2 THEN CONCAT('Acuerdo ', doc.id)
                                    WHEN doc.tipoDocumento IS NULL THEN 'Demanda inicial'
                                    ELSE COALESCE(CONCAT('promo ', doc.folio), 'Demanda inicial')
                                END,
                                doc.ruta,
                                m.recomendaciones,
                                CASE WHEN doc.acuerdoRespuesta IS NOT NULL THEN 1 ELSE 0 END
                            )
                            FROM Documento doc
                            JOIN doc.carpeta carpeta
                            LEFT JOIN doc.concepto concepto
                            LEFT JOIN Movimiento m ON m.documento = doc
                            WHERE doc.carpeta.id = :carpetaId
                              AND (
                                (:tipoDocumento = 'ACUERDO' AND (doc.tipoDocumento IS NULL OR doc.tipoDocumento = 0)) OR
                                (:tipoDocumento = 'SENTENCIA' AND (doc.tipoDocumento IS NULL OR doc.tipoDocumento IN (0, 2)))
                              )
                              AND (
                                (:tipoDocumento = 'SENTENCIA' AND doc.tipoDocumento = 2 AND doc.concepto IS NULL)
                                OR concepto.nombre = 'Adjuntar'
                                OR doc.tipoDocumento IS NULL
                              )
                              AND (
                                (doc.tipoDocumento = 2 AND m.estado = 'CREADO' AND m.id = (
                                    SELECT MAX(m2.id)
                                    FROM Movimiento m2
                                    WHERE m2.documento = doc
                                )) OR
                                doc.tipoDocumento IS NULL OR
                                (m.estado = 'ASIGNADO' AND m.id = (
                                    SELECT MAX(m3.id)
                                    FROM Movimiento m3
                                    WHERE m3.documento = doc
                                ))
                              )
                              AND (
                                (:documentoId IS NULL AND doc.acuerdoRespuesta IS NULL) OR
                                (:documentoId IS NOT NULL AND (:documentoId = doc.acuerdoRespuesta.id OR doc.acuerdoRespuesta IS NULL))
                              )
                        """)
        List<AcuerdoPromocionesRecord> obtenerPromociones(
                        @Param("carpetaId") Integer carpetaId,
                        @Param("documentoId") Integer documentoId,
                        @Param("tipoDocumento") String tipoDocumento);

        @Modifying
        @Query("UPDATE Documento doc SET doc.acuerdoRespuesta = null WHERE doc.carpeta.id = :carpetaId AND doc.acuerdoRespuesta.id = :documentoId")
        void actualizacionAcuerdoRespuesta(@Param("carpetaId") Integer carpetaId,
                        @Param("documentoId") Integer documentoId);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdosRecord(
                        doc.id,
                        dd.fechaPublicacion,
                        dd.resumen,
                        doc.estatus,
                        LEFT(dd.extractoSentencia, 20),
                        doc.tipoDocumento
                        )
                        FROM DocumentoDetalle dd
                        JOIN dd.documento doc
                        WHERE doc.tipoDocumento IN (TipoDocumento.ACUERDO, TipoDocumento.SENTENCIA)  AND doc.carpeta.id = :carpetaId
                        """)
        Page<AcuerdosRecord> findAllAcuerdosYSentenciasByCarpeta(Integer carpetaId, Pageable pageable);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord(
                        pd.id,
                        concat(pd.nombre, ' ', pd.apellidoPaterno, ' ', pd.apellidoMaterno),
                        tp.nombre,
                        pd.tipoNotificacion )
                        FROM PersonaDocumento pd
                        JOIN pd.tipoPartes tp
                        WHERE pd.carpeta.id = :carpetaId
                        AND (
                            (:tipoParte = 'ambos' AND (lower(tp.nombre) LIKE '%actor%' OR lower(tp.nombre) LIKE '%demandado%'))
                            OR
                            (:tipoParte = 'actor' AND lower(tp.nombre) LIKE '%actor%')
                            OR
                            (:tipoParte = 'demandado' AND lower(tp.nombre) LIKE '%demandado%')
                            OR
                            (:tipoParte = 'otros' AND lower(tp.nombre) NOT LIKE '%actor%' AND lower(tp.nombre) NOT LIKE '%demandado%')
                        )
                        """)
        List<AcuerdoNotificadosRecord> findTipoPartesAcuerdo(Integer carpetaId, String tipoParte);

        @Query("""
                        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta(
                            d.id,
                            d.folio,
                            d.tipoDocumento,
                            null,
                            COALESCE(m.fechaAsignacion, d.audit.fechaAlta),
                            d.ruta,
                            COALESCE(p.id, COALESCE(d.persona.id,(SELECT id FROM Persona p where p.usuario=d.audit.usuarioAlta))),
                            d.carpeta.tipoCarpeta,
                            d.estatus
                        )
                        FROM Documento d
                        LEFT JOIN Movimiento m on m.estado = 'CAPTURA' and m.documento = d
                        LEFT JOIN m.persona p
                        WHERE
                            d.carpeta.id=:carpetaId
                            AND
                            CASE WHEN d.tipoDocumento IS NULL OR d.tipoDocumento != TipoDocumento.PROMOCION or d.carpeta.tipoCarpeta = TipoCarpeta.PIEZA THEN 1
                                WHEN d.tipoDocumento = TipoDocumento.PROMOCION AND d.estatus = EstadoCarpeta.INTEGRADO THEN 1
                                ELSE 0 END = 1
                        """)
        List<DocumentoDetalleCarpeta> findDocumentosByCarpeta(Integer carpetaId);

        List<Documento> findByCarpetaId(Integer carpetaId);

        List<Documento> findByAcuerdoRespuestaId(Integer acuerdoId);

        @Query("""
                            SELECT d
                            FROM Carpeta c
                            JOIN Documento d ON d.carpeta.id = c.id
                            WHERE c.expediente = :expediente
                              AND d.tipoDocumento = :tipoDocumento
                              AND c.juzgado.id = :juzgadoId
                        """)
        Optional<Documento> findByExpedienteAndTipoDocumento(String expediente, TipoDocumento tipoDocumento,
                        Integer juzgadoId);

        Integer countByCarpetaIdAndTipoDocumentoAndAuditFechaAltaAfter(Integer carpetaId, TipoDocumento tipoDocumento,
                        LocalDateTime fechaAlta);

        Integer countByCarpetaIdAndTipoDocumentoAndEstatus(int carpetaId, TipoDocumento tipo, EstadoCarpeta estado);

        @Query("""
                        SELECT d
                        FROM Documento d
                        JOIN DocumentoContenido dc ON dc.documento.id = d.id
                        JOIN d.carpeta c
                        WHERE lower(dc.oficioPublicado) = 's'
                          AND c.id = :carpetaId
                          AND d.tipoDocumento = TipoDocumento.SENTENCIA
                        """)
        Optional<Documento> findSentenciaPublicadaByCarpetaId(@Param("carpetaId") Integer carpetaId);

        Page<Documento> findByCarpetaIdAndTipoDocumentoIn(Integer carpetaId, List<TipoDocumento> tiposDocumento,
                        Pageable pageable);

        @Query("""
                            SELECT doc
                            FROM Documento doc
                            JOIN doc.carpeta ca
                            JOIN ca.juzgado juz
                            JOIN PersonaDocumento pd ON pd.carpeta = ca
                            WHERE LOWER(pd.correoNotificacion) = LOWER(:correo)
                            AND doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                            AND (lower(juz.nombre) LIKE %:key% OR lower(ca.expediente) LIKE %:key%)
                        """)
        List<Documento> findPromocionesLitigante(@Param("correo") String correo, @Param("key") String key);

        @Query("""
                            SELECT CASE WHEN COUNT(doc) > 0 THEN true ELSE false END
                            FROM Documento doc
                            JOIN doc.persona p
                            WHERE doc.carpeta.expediente = :expediente
                            AND doc.folio = :numeroAcuerdo
                            AND LOWER(p.correoElectronico) = LOWER(:correo)
                        """)
        boolean existsByExpedienteAndAcuerdoAndAsociateCorreo(@Param("expediente") String expediente,
                        @Param("numeroAcuerdo") String numeroAcuerdo,
                        @Param("correo") String correo);

        @Query("""
                            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records.BandejaEnviosRecord(
                                doc.id,
                                doc.folio,
                                institucion.nombre,
                                juzgado.nombre,
                                COALESCE(persona.nombre, 'Sin asignar'),
                                doc.estatus,
                                documentoDetalle.estadoEnvio
                            )
                            FROM DocumentoDetalle documentoDetalle
                            LEFT JOIN documentoDetalle.persona persona
                            JOIN documentoDetalle.documento doc
                            JOIN doc.institucion institucion
                            JOIN doc.carpeta carpeta
                            JOIN carpeta.juzgado juzgado
                            WHERE doc.tipoDocumento = TipoDocumento.OFICIO AND doc.estatus = 9
                            AND (
                                CAST(doc.id AS text) = :key OR :key = ''
                            )
                            AND (:isOficialMayorOficialia = true AND documentoDetalle.estadoEnvio IS NOT NULL
                                OR :isOficialMayorOficialia = false
                                )
                            AND juzgado IN :juzgados

                        """)
        List<BandejaEnviosRecord> findAllOficiosBandejaSalida(
                        @Param("key") String key,
                        @Param("isOficialMayorOficialia") Boolean isOficialMayorOficialia,
                        @Param("juzgados") List<Juzgado> juzgados);

        Optional<Documento> findByTipoDocumentoAndFolio(TipoDocumento tipodocumento, String folio);
        Optional<Documento> findByTipoDocumentoAndCarpetaId(TipoDocumento tipoDocumento, Integer carpetaId);
    @Query("""
                SELECT COUNT(d)
                FROM Documento d
                  JOIN d.carpeta c
                  JOIN c.juzgado j
                  JOIN j.materia m
                  WHERE d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.SENTENCIA
                  AND m.nombre IN :materias
            """)
    Long countDocumentosPorTipoYMaterias(@Param("materias") List<String> materias);

    @Query(
            value = """
                    SELECT
                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.pn_id
                            ELSE c.pn_id
                        END AS id,

                        j.s_nombre AS juzgado,

                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN 'DOCUMENTO'
                            ELSE 'CARPETA'
                        END AS tipo,

                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.n_tipo_documento
                            ELSE c.n_tipo_carpeta
                        END AS tipoId,

                        c.s_expediente AS expediente,

                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.t_fecha_alta
                            ELSE c.t_fecha_alta
                        END AS fechaAlta,

                        COALESCE(
                            STRING_AGG(a.s_nombre, E'\\n' ORDER BY a.s_nombre),
                            ''
                        ) AS anexos,
                        p.n_paquete_id AS paqueteId
                    FROM trials.tbl_documentos d
                    JOIN trials.tbl_carpetas c ON c.pn_id = d.fn_carpeta
                    JOIN trials.tbl_juzgados j ON j.pn_id = c.fn_juzgado
                    LEFT JOIN trials.tbl_anexos a ON a.fn_documento = d.pn_id
                    LEFT JOIN trials.tbl_paquetes p
                        ON p.pn_id = COALESCE(d.fn_paquete, c.fn_paquete)

                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 13
                        )
                        OR
                        (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 13
                        )

                    GROUP BY
                        d.pn_id,
                        c.pn_id,
                        j.s_nombre,
                        d.n_tipo_documento,
                        c.n_tipo_carpeta,
                        c.s_expediente,
                        d.t_fecha_alta,
                        c.t_fecha_alta,
                        p.n_paquete_id
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM tbl_documentos d
                    JOIN tbl_carpetas c ON c.pn_id = d.fn_carpeta
                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 13
                        )
                        OR
                        (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 13
                        )
                    """,
            nativeQuery = true
    )
    Page<ArchivoJudicialProjection> findArchivoJudicialList(Pageable pageable);

    @Query(
            value = """
                    SELECT
                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.pn_id
                            ELSE c.pn_id
                        END AS id,

                        j.s_nombre AS juzgado,

                        c.s_expediente AS expediente,

                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN 'DOCUMENTO'
                            ELSE 'CARPETA'
                        END AS tipo,

                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.n_tipo_documento
                            ELSE c.n_tipo_carpeta
                        END AS tipoId,

                        mov_rec.t_fecha_asignacion AS fechaRecepcion,

                        act.actorNombre AS actorPrincipal,
                        dem.demandadoNombre AS demandadoPrincipal,
                        paq.n_paquete_id AS paqueteId
                    FROM trials.tbl_documentos d
                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = d.fn_carpeta
                    JOIN trials.tbl_juzgados j
                        ON j.pn_id = c.fn_juzgado
                    LEFT JOIN trials.tbl_paquetes paq
                        ON paq.pn_id = COALESCE(d.fn_paquete, c.fn_paquete)

                    LEFT JOIN LATERAL (
                        SELECT m.t_fecha_asignacion
                        FROM trials.tbl_movimientos m
                        WHERE m.s_estado = 'ARCHIVO_JUDICIAL_RECIBIDO'
                          AND (
                              (m.fn_documento IS NOT NULL AND m.fn_documento = d.pn_id)
                              OR
                              (m.fn_carpeta IS NOT NULL AND m.fn_carpeta = c.pn_id)
                          )
                        ORDER BY m.t_fecha_asignacion DESC
                        LIMIT 1
                    ) mov_rec ON TRUE

                    LEFT JOIN LATERAL (
                        SELECT
                            COALESCE(
                                NULLIF(
                                    CONCAT_WS(
                                        ' ',
                                        p.s_nombres,
                                        p.s_apellido_paterno,
                                        p.s_apellido_materno
                                    ),
                                    ''
                                ),
                                p.s_pseudonimo
                            ) AS actorNombre
                        FROM trials.tbl_personas_documentos p
                        JOIN trials.tbl_tipo_partes tp
                            ON tp.pn_id = p.fn_tipo_parte
                        WHERE p.fn_carpeta = c.pn_id
                          AND p.n_rol = 0
                          AND tp.s_nombre = 'Actor'
                        ORDER BY p.pn_id
                        LIMIT 1
                    ) act ON TRUE

                    LEFT JOIN LATERAL (
                        SELECT
                            COALESCE(
                                NULLIF(
                                    CONCAT_WS(
                                        ' ',
                                        p.s_nombres,
                                        p.s_apellido_paterno,
                                        p.s_apellido_materno
                                    ),
                                    ''
                                ),
                                p.s_pseudonimo
                            ) AS demandadoNombre
                        FROM trials.tbl_personas_documentos p
                        JOIN trials.tbl_tipo_partes tp
                            ON tp.pn_id = p.fn_tipo_parte
                        WHERE p.fn_carpeta = c.pn_id
                          AND p.n_rol = 0
                          AND tp.s_nombre = 'Demandado'
                        ORDER BY p.pn_id
                        LIMIT 1
                    ) dem ON TRUE

                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 16
                        )
                        OR (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 16
                        )

                    ORDER BY mov_rec.t_fecha_asignacion DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM trials.tbl_documentos d
                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = d.fn_carpeta
                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 16
                        )
                        OR (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 16
                        )
                    """,
            nativeQuery = true
    )
    Page<RecibidosProjection> findArchivoJudicialRecibidos(Pageable pageable);

    @Query(
            value = """
                    SELECT
                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.pn_id
                            ELSE c.pn_id
                        END AS id,
                        j.s_nombre AS juzgado,
                        c.s_expediente AS expediente,
                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN 'DOCUMENTO'
                            ELSE 'CARPETA'
                        END AS tipo,
                        CASE
                            WHEN d.n_tipo_documento IS NOT NULL THEN d.n_tipo_documento
                            ELSE c.n_tipo_carpeta
                        END AS tipoId,
                        act.actorNombre       AS actorPrincipal,
                        dem.demandadoNombre  AS demandadoPrincipal,
                        p.n_urgente           AS urgente,
                        p.t_fecha_envio       AS fechaSolicitud
                    FROM trials.tbl_documentos d
                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = d.fn_carpeta
                    JOIN trials.tbl_juzgados j
                        ON j.pn_id = c.fn_juzgado
                    LEFT JOIN trials.tbl_paquetes p
                        ON p.pn_id = COALESCE(d.fn_paquete, c.fn_paquete)
                    LEFT JOIN LATERAL (
                        SELECT
                            m.t_fecha_asignacion
                        FROM trials.tbl_movimientos m
                        WHERE m.s_estado = 'ARCHIVO_JUDICIAL_SOLICIT'
                          AND (
                              (m.fn_documento IS NOT NULL AND m.fn_documento = d.pn_id)
                              OR
                              (m.fn_carpeta IS NOT NULL AND m.fn_carpeta = c.pn_id)
                          )
                        ORDER BY m.t_fecha_asignacion DESC
                        LIMIT 1
                    ) mov_rec ON TRUE
                    LEFT JOIN LATERAL (
                        SELECT
                            COALESCE(
                                NULLIF(
                                    CONCAT_WS(
                                        ' ',
                                        p.s_nombres,
                                        p.s_apellido_paterno,
                                        p.s_apellido_materno
                                    ),
                                    ''
                                ),
                                p.s_pseudonimo
                            ) AS actorNombre
                        FROM trials.tbl_personas_documentos p
                        JOIN trials.tbl_tipo_partes tp
                            ON tp.pn_id = p.fn_tipo_parte
                        WHERE p.fn_carpeta = c.pn_id
                          AND p.n_rol = 0
                          AND tp.s_nombre = 'Actor'
                        ORDER BY p.pn_id
                        LIMIT 1
                    ) act ON TRUE
                    LEFT JOIN LATERAL (
                        SELECT
                            COALESCE(
                                NULLIF(
                                    CONCAT_WS(
                                        ' ',
                                        p.s_nombres,
                                        p.s_apellido_paterno,
                                        p.s_apellido_materno
                                    ),
                                    ''
                                ),
                                p.s_pseudonimo
                            ) AS demandadoNombre
                        FROM trials.tbl_personas_documentos p
                        JOIN trials.tbl_tipo_partes tp
                            ON tp.pn_id = p.fn_tipo_parte
                        WHERE p.fn_carpeta = c.pn_id
                          AND p.n_rol = 0
                          AND tp.s_nombre = 'Demandado'
                        ORDER BY p.pn_id
                        LIMIT 1
                    ) dem ON TRUE
                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 17
                        )
                        OR (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 17
                        )
                    ORDER BY p.t_fecha_envio DESC;
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM trials.tbl_documentos d
                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = d.fn_carpeta
                    WHERE
                        (
                            d.n_tipo_documento IS NOT NULL
                            AND d.n_estado = 17
                        )
                        OR (
                            d.n_tipo_documento IS NULL
                            AND c.n_estado = 17
                        )
                    """,
            nativeQuery = true
    )
    Page<SolicitudesProjection> findArchivoJudicialSolicitudes(Pageable pageable);
}