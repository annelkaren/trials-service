package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAsignadoRecord;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoJuzgadoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSalidaRecord;
import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
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
            AND m.motivo = 'SALIDA'
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
    Page<DocumentoSalidaRecord> findByEstatusSalida(String key, Integer oficialiaId, Integer juzgadoId, Integer folio, TipoCarpeta  tipoCarpeta, TipoDocumento tipoDocumento, Pageable pageable);

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

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAsignadoRecord(
            d.id,
            c.expediente,
            c.folio,
            d.folio,
            c.tipoCarpeta,
            d.tipoDocumento,
            d.concepto,
            c.fechaAsignacion,
            c.estatus,
            ''
        )
        FROM Documento d
        JOIN d.carpeta c on c.persona=:personaAsignada
        where case when :key is null then 1
            when c.expediente like %:key% or c.folio like %:key% or d.concepto.nombre like %:key% then 1
            else 0 end = 1
            AND c.estatus in( mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.TURNADO,
            mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.ASIGNADO,
            mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.DEVUELTO)
        """)
    Page<DocumentoAsignadoRecord> findByPersonaAsignada(String key, Persona personaAsignada, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Documento d SET d.estatus = :estado WHERE d.id = :documentoId")
    void actualizarEstatus(@Param("documentoId") Integer documentoId, @Param("estado") EstadoCarpeta estado);
}
