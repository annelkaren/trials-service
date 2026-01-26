package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.workflow.archivojudicial.ArchivoJudicialHistoricoProjection;
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
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.BandejasQueries;
import mx.gob.pjpuebla.trials.workflow.documentos.records.AcusePromocionDetailRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaEntradaRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaHistorialRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSalidaResponseRecord;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer>, JpaSpecificationExecutor<Movimiento> {

  //Bandejas de OFICIALIAS:
  @Query(value = BandejasQueries.QUERY_BANDEJA_ENTRADA)
  Page<BandejaEntradaRecord> getBandejaEntradaPage(
      Pageable pageable,

      @Param("juzgadoId") Integer juzgadoId,
      @Param("oficialiaId") Integer oficialiaId,

      @Param("key") String key,
      @Param("cmdLetra") String cmdLetra,
      @Param("cmdFolio") String cmdFolio,

      @Param("folio") String folio,
      @Param("expediente") String expediente,
      @Param("materia") String materia,
      @Param("tipoEntrada") String tipoEntrada,
      @Param("organoJurisdiccional") String organoJurisdiccional);

  @Query(value = BandejasQueries.QUERY_BANDEJA_SALIDA)
  Page<DocumentoSalidaResponseRecord> getBandejaSalidaPage(
      Pageable pageable,

      @Param("key") String key,
      @Param("cmdLetra") String cmdLetra,
      @Param("cmdFolio") String cmdFolio,

      @Param("folio") String folio,
      @Param("expediente") String expediente,
      @Param("materia") String materia,
      @Param("tipoEntrada") String tipoEntrada,
      @Param("organoJurisdiccional") String organoJurisdiccional,

      @Param("fechaFrom") LocalDateTime fechaFrom,
      @Param("fechaTo") LocalDateTime fechaTo,

      @Param("oficialiaId") Integer oficialiaId,
      @Param("juzgadoId") Integer juzgadoId);

  @Query(value = BandejasQueries.QUERY_BANDEJA_HISTORIAL)
  Page<BandejaHistorialRecord> getBandejaHistorialPage(
      Pageable pageable,


      @Param("oficialiaId") Integer oficialiaId,
      @Param("juzgadoId") Integer juzgadoId,

      // key + cmd
      @Param("key") String key,
      @Param("cmdLetra") String cmdLetra,
      @Param("cmdFolio") String cmdFolio,

      // filtros por columna
      @Param("folio") String folio,
      @Param("expediente") String expediente,
      @Param("materia") String materia,
      @Param("tipoEntrada") String tipoEntrada,
      @Param("organoJurisdiccional") String organoJurisdiccional,

      // fechas
      @Param("fechaFrom") LocalDateTime fechaFrom,
      @Param("fechaTo") LocalDateTime fechaTo);

  @Query(value = BandejasQueries.QUERY_BANDEJA_HISTORIAL)
  Page<BandejaHistorialRecord> getBandejaHistorialArchivoJudicial(
          Pageable pageable,
          @Param("oficialiaId") Integer oficialiaId,
          @Param("juzgadoId") Integer juzgadoId,
          // key + cmd
          @Param("key") String key,
          @Param("cmdLetra") String cmdLetra,
          @Param("cmdFolio") String cmdFolio,
          // filtros por columna
          @Param("folio") String folio,
          @Param("expediente") String expediente,
          @Param("materia") String materia,
          @Param("tipoEntrada") String tipoEntrada,
          @Param("organoJurisdiccional") String organoJurisdiccional,
          // fechas
          @Param("fechaFrom") LocalDateTime fechaFrom,
          @Param("fechaTo") LocalDateTime fechaTo);

  // Bandejas de juzgados:
  @Query(value = BandejasQueries.QUERY_BANDEJA_RECEPCION)
  Page<DocumentoBandejaRecepcionRecord> getBandejaRecepcionPage(
      Pageable pageable,

      @Param("juzgadoId") Integer juzgadoId,
      @Param("estadoList") List<EstadoCarpeta> estadoList,

      @Param("personaId") Persona personaId,

      // modo
      @Param("isOficialMayor") boolean isOficialMayor,
      @Param("motivoSingle") String motivoSingle,
      @Param("motivosList") List<String> motivosList,

      // buscador + cmd
      @Param("key") String key,
      @Param("cmdLetra") String cmdLetra,
      @Param("cmdFolio") String cmdFolio,

      // filtros por columna
      @Param("folio") String folio,
      @Param("expediente") String expediente,
      @Param("tipoEntrada") String tipoEntrada,
      @Param("origen") String origen,
      @Param("motivoTurnado") String motivoTurnado,
      @Param("fechaFrom") LocalDateTime fechaFrom,
      @Param("fechaTo") LocalDateTime fechaTo,

      // para isInterno en oficial mayor
      @Param("userJuzgadoNombre") String userJuzgadoNombre,
      @Param("userOficialiaNombre") String userOficialiaNombre);

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

    @Query(
            value = """
                    SELECT
                        mov.pn_id AS id,
                        CASE
                            WHEN mov.fn_documento IS NOT NULL THEN 'DOCUMENTO'
                            ELSE 'CARPETA'
                        END AS tipoEntidad,

                        CASE
                            WHEN mov.fn_documento IS NOT NULL THEN d.s_folio
                            ELSE c.s_folio
                        END AS folio,

                        c.s_expediente AS expediente,

                        mov.s_estado AS estadoMovimiento,

                        mat.s_nombre AS materia,

                        CASE
                            WHEN mov.fn_documento IS NOT NULL THEN d.n_tipo_documento
                            ELSE c.n_tipo_carpeta
                        END AS tipoId,

                        mov.t_fecha_asignacion AS fechaHora

                    FROM trials.tbl_movimientos mov

                    LEFT JOIN trials.tbl_documentos d
                        ON d.pn_id = mov.fn_documento

                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = COALESCE(d.fn_carpeta, mov.fn_carpeta)

                    JOIN trials.tbl_juzgados j
                        ON j.pn_id = c.fn_juzgado

                    JOIN trials.tbl_materias mat
                        ON mat.pn_id = j.fn_materia

                    WHERE mov.s_estado LIKE '%ARCHIVO_JUDICIAL%'

                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM trials.tbl_movimientos mov
                    LEFT JOIN trials.tbl_documentos d
                        ON d.pn_id = mov.fn_documento
                    JOIN trials.tbl_carpetas c
                        ON c.pn_id = COALESCE(d.fn_carpeta, mov.fn_carpeta)
                    WHERE mov.s_estado LIKE '%ARCHIVO_JUDICIAL%'
                    """,
            nativeQuery = true
    )
    Page<ArchivoJudicialHistoricoProjection> findHistoricoArchivoJudicial(Pageable pageable);
}
