package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdosRecord;
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
             SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSalidaRecord(
                m.id,
                COALESCE(c.id, doc_carpeta.id),
                COALESCE(c.folio, doc.folio),
                COALESCE(c.expediente, doc_carpeta.expediente),
                COALESCE(jc.id, jd.id),
                COALESCE(jc.nombre, jd.nombre),
                COALESCE(matc.nombre, matd.nombre),
                c.tipoCarpeta,
                doc.tipoDocumento,
                COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta),
                COALESCE(c.selloEstatus, doc_carpeta.selloEstatus),
                COALESCE(c.estatus, doc_carpeta.estatus)
            )
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN jc.materia matc
            LEFT JOIN m.documento doc
            LEFT JOIN doc.carpeta doc_carpeta
            LEFT JOIN doc_carpeta.juzgado jd
            LEFT JOIN jd.materia matd
            WHERE m.fechaAsignacion = (
                SELECT MAX(m2.fechaAsignacion)
                FROM Movimiento m2
                WHERE
                (
                    (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id) OR
                    (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
                )
            )
            AND m.estado = 'SALIDA'
            AND (m.oficialia.id = :oficialiaId OR m.juzgado.id = :juzgadoId)
             AND (
                  ((:tipoCarpeta IS NOT NULL AND COALESCE(c.folio, doc.folio) = :folio AND c.tipoCarpeta = :tipoCarpeta)
                         OR (:tipoDocumento IS NOT NULL AND COALESCE(c.folio, doc.folio) = :folio AND doc.tipoDocumento = :tipoDocumento))
                   OR lower(COALESCE(jc.nombre, jd.nombre)) LIKE %:key%
                   OR lower(COALESCE(c.folio, doc.folio)) = lower(:key)
                   OR lower(COALESCE(c.expediente, doc_carpeta.expediente)) LIKE %:key%
               )
            ORDER BY
                COALESCE(jc.nombre, jd.nombre) ASC,
                COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta) DESC,
                COALESCE(c.tipoCarpeta, doc_carpeta.tipoCarpeta) ASC
            """)
    Page<DocumentoSalidaRecord> findByEstatusSalida(String key, Integer oficialiaId, Integer juzgadoId, Integer folio,
            TipoCarpeta tipoCarpeta, TipoDocumento tipoDocumento, Pageable pageable);

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
                    ) AND pc = :personaAsignada
                    AND jc.id = :juzgadoId)
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
                )
            """)
    Page<Movimiento> findByPersonaAsignada(String key, Integer juzgadoId, Persona personaAsignada, boolean isOficial,
            Pageable pageable);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.OficioResponseRecord(
                doc.id,
                doc.folio,
                ins.nombre,
                dd.asunto,
                doc.estatus,
                dd.fechaEmision,
                dd.fechaEntrega,
                CASE WHEN dd.ruta IS NOT NULL THEN true ELSE false END,
                CASE WHEN COUNT(dc) > 0 THEN true ELSE false END,
                MAX(dc.tamanioPapel)
            )
            FROM Documento doc
            LEFT JOIN doc.institucion ins
            LEFT JOIN DocumentoDetalle dd ON dd.documento = doc
            LEFT JOIN DocumentoContenido dc ON dc.documento = doc
            WHERE doc.tipoDocumento = :tipoDocumento
            AND (
                lower(doc.folio) LIKE %:key% OR
                lower(ins.nombre) LIKE %:key% OR
                lower(dd.asunto) LIKE %:key%
            )
            GROUP BY doc.id, ins.nombre, dd.asunto, doc.estatus, dd.fechaEmision, dd.fechaEntrega, dd.ruta
            """)
    Page<OficioResponseRecord> findAllByTipoDocumento(String key, TipoDocumento tipoDocumento, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Documento d SET d.estatus = :estado WHERE d.id = :documentoId")
    void actualizarEstatus(@Param("documentoId") Integer documentoId, @Param("estado") EstadoCarpeta estado);

    //colocamos id al acerdo momentaneamente ya que no se genera actualmente folio
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
        WHERE
            (
                (:tipoDocumento = 'ACUERDO' AND (doc.tipoDocumento = 0 OR doc.tipoDocumento IS NULL)) OR
                (:tipoDocumento = 'SENTENCIA' AND (doc.tipoDocumento IN (0, 2) OR doc.tipoDocumento IS NULL))
            )
            AND (
                (:tipoDocumento = 'SENTENCIA' AND (doc.tipoDocumento = 2 AND doc.concepto IS NULL))
                OR
                (concepto.nombre = 'Adjuntar'
                 OR (doc.tipoDocumento IS NULL AND concepto.nombre = 'Distribución')
                 OR (doc.tipoDocumento = 2 AND :tipoDocumento = 'SENTENCIA' AND doc.concepto IS NULL)
                )
            )
            AND (
                ((doc.tipoDocumento = 2 AND m.estado = 'CREADO') OR doc.tipoDocumento IS NULL)
                OR
                (m.estado = 'ASIGNADO')
            )
            AND doc.carpeta.id = :carpetaId
            AND
            CASE
                WHEN :documentoId IS NULL AND doc.acuerdoRespuesta IS NULL THEN 1
                WHEN :documentoId IS NOT NULL AND (:documentoId = doc.acuerdoRespuesta.id) OR (doc.acuerdoRespuesta IS NULL) THEN 1
                ELSE 0
            END = 1
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
    Optional<Documento> findByExpedienteAndTipoDocumento(String expediente, TipoDocumento tipoDocumento, Integer juzgadoId);

    Integer countByCarpetaIdAndTipoDocumentoAndAuditFechaAltaAfter(Integer carpetaId, TipoDocumento tipoDocumento, LocalDateTime fechaAlta);

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


}