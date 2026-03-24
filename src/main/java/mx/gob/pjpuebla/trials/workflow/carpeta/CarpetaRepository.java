package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesRecord;
import mx.gob.pjpuebla.trials.litigante.responselitigante.PiezaRecord;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.LibroGobiernoRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {
  String QUERY_LIBRO_GOBIERNO = """
      SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.LibroGobiernoRecord(
                      c.id,
                      c.expediente,
                      c.audit.fechaAlta,
                      tj.nombre,(SELECT MIN(
                          CONCAT(
                            COALESCE(pd.nombre, ''), ' ',
                            COALESCE(pd.apellidoPaterno, ''), ' ',
                            COALESCE(pd.apellidoMaterno, '')
                          )
                        )
                        FROM PersonaDocumento pd
                        JOIN pd.tipoPartes tp
                        WHERE pd.carpeta.id = c.id
                          AND tp.nombre = 'Actor'
                          AND pd.rol = mx.gob.pjpuebla.trials.util.enums.Rol.PRINCIPAL
                      ),
                      (SELECT MIN(
                          CONCAT(
                            COALESCE(pd.nombre, ''), ' ',
                            COALESCE(pd.apellidoPaterno, ''), ' ',
                            COALESCE(pd.apellidoMaterno, '')
                          )
                        )
                        FROM PersonaDocumento pd
                        JOIN pd.tipoPartes tp
                        WHERE pd.carpeta.id = c.id
                          AND tp.nombre = 'Demandado'
                          AND pd.rol = mx.gob.pjpuebla.trials.util.enums.Rol.PRINCIPAL
                      ),

                      CASE
                        WHEN c.persona.id = :userId AND c.estatus = :estadoAsignado THEN true
                        ELSE false
                      END,

                      cd.cujus,
                      m.estatus,
                      c.migrado
                  )
                  FROM Carpeta c
                  JOIN c.tipoJuicio tj
                  JOIN c.juzgado j
                  LEFT JOIN CarpetaDetalle cd ON cd.carpeta.id = c.id
                  LEFT JOIN Migraciones m ON m.carpeta.id = c.id
                  WHERE

      c.juzgado IN (:juzgados)
                  AND (
                    COALESCE(:expediente,'') = '' OR
                    LOWER(c.expediente)
                    LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
                  )

                 AND c.audit.fechaAlta >= COALESCE(:fechaFrom, c.audit.fechaAlta)
                AND c.audit.fechaAlta <= COALESCE(:fechaTo,   c.audit.fechaAlta)
                  AND (
                    COALESCE(:tipoJuicio,'') = '' OR
                    LOWER(tj.nombre)

      LIKE CONCAT('%', LOWER(COALESCE(:tipoJuicio,'')), '%')
                  )

                  AND (
                    COALESCE(:cujus,'') = '' OR
                    LOWER(COALESCE(cd.cujus,''))

      LIKE CONCAT('%', LOWER(COALESCE(:cujus,'')), '%')
                  )

                  AND (
                    COALESCE(:actor,'') = ''

      OR EXISTS (
                      SELECT 1
                      FROM PersonaDocumento pd
                      JOIN pd.tipoPartes tp
                      WHERE pd.carpeta.id = c.id
                        AND tp.nombre = 'Actor'
                        AND (
                          LOWER(pd.nombre)

      LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%') OR
                          LOWER(pd.apellidoPaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%') OR
                          LOWER(pd.apellidoMaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%')
                        )
                    )
                  )

                  AND (
                    COALESCE(:demandado,'') = ''

      OR EXISTS (
                      SELECT 1
                      FROM PersonaDocumento pd
                      JOIN pd.tipoPartes tp
                      WHERE pd.carpeta.id = c.id
                        AND tp.nombre = 'Demandado'
                        AND (
                          LOWER(pd.nombre)

      LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%') OR
                          LOWER(pd.apellidoPaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%') OR
                          LOWER(pd.apellidoMaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%')
                        )
                    )
                  )

                  AND (
                    COALESCE(:key,'') = '' OR (
                      LOWER(c.expediente)

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')

      OR LOWER(tj.nombre)

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')

      OR LOWER(COALESCE(cd.cujus,''))

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')

      OR EXISTS (
                        SELECT 1
                        FROM PersonaDocumento pd
                        WHERE pd.carpeta.id = c.id
                          AND (
                            LOWER(pd.nombre)

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')

      OR LOWER(pd.apellidoPaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')

      OR LOWER(pd.apellidoMaterno)

      LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
                          )
                      )
                    )
                  )""";

  String COUNT_LIBRO_GOBIERNO = """
      SELECT COUNT(c.id)
      FROM Carpeta c
      JOIN c.tipoJuicio tj
      JOIN c.juzgado j
      LEFT JOIN CarpetaDetalle cd ON cd.carpeta.id = c.id
      WHERE c.juzgado IN (:juzgados)

      AND (
        COALESCE(:expediente,'') = '' OR
        LOWER(c.expediente) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
      )

      AND c.audit.fechaAlta >= COALESCE(:fechaFrom, c.audit.fechaAlta)
      AND c.audit.fechaAlta <= COALESCE(:fechaTo,   c.audit.fechaAlta)

      AND (
        COALESCE(:tipoJuicio,'') = '' OR
        LOWER(tj.nombre) LIKE CONCAT('%', LOWER(COALESCE(:tipoJuicio,'')), '%')
      )

      AND (
        COALESCE(:cujus,'') = '' OR
        LOWER(COALESCE(cd.cujus,'')) LIKE CONCAT('%', LOWER(COALESCE(:cujus,'')), '%')
      )

      AND (
        COALESCE(:actor,'') = '' OR EXISTS (
          SELECT 1
          FROM PersonaDocumento pd
          JOIN pd.tipoPartes tp
          WHERE pd.carpeta.id = c.id
            AND tp.nombre = 'Actor'
            AND (
              LOWER(pd.nombre) LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%') OR
              LOWER(pd.apellidoPaterno) LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%') OR
              LOWER(pd.apellidoMaterno) LIKE CONCAT('%', LOWER(COALESCE(:actor,'')), '%')
            )
        )
      )

      AND (
        COALESCE(:demandado,'') = '' OR EXISTS (
          SELECT 1
          FROM PersonaDocumento pd
          JOIN pd.tipoPartes tp
          WHERE pd.carpeta.id = c.id
            AND tp.nombre = 'Demandado'
            AND (
              LOWER(pd.nombre) LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%') OR
              LOWER(pd.apellidoPaterno) LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%') OR
              LOWER(pd.apellidoMaterno) LIKE CONCAT('%', LOWER(COALESCE(:demandado,'')), '%')
            )
        )
      )

      AND (
        COALESCE(:key,'') = '' OR (
          LOWER(c.expediente) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
          OR LOWER(tj.nombre) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
          OR LOWER(COALESCE(cd.cujus,'')) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
          OR EXISTS (
            SELECT 1
            FROM PersonaDocumento pd
            WHERE pd.carpeta.id = c.id
              AND (
                LOWER(pd.nombre) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
                OR LOWER(pd.apellidoPaterno) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
                OR LOWER(pd.apellidoMaterno) LIKE CONCAT('%', LOWER(COALESCE(:key,'')), '%')
              )
          )
        )
      )
      """;

  @Query("""
      SELECT c
      FROM Carpeta c
      WHERE c.expediente = :expediente
      AND c.juzgado.id = :juzgadoId
      """)
  Optional<Carpeta> findByExpedienteAndJuzgadoId(String expediente, Integer juzgadoId);

  @Query("""
      SELECT c
      FROM Carpeta c
      WHERE c.expediente =:expediente
      AND c.juzgado.id = :juzgadoId
      """)
  Optional<Carpeta> findByExpedienteNormalizadoAndJuzgadoId(String expediente, Integer juzgadoId);

  @Query("""
      SELECT c
      FROM Carpeta c
      WHERE c.expediente ilike :expediente%
      AND c.expediente ilike %:nomenclatura
      AND c.juzgado.id = :juzgadoId
      """)
  Optional<Carpeta> findByExpedienteAndJuzgadoIdPenal(String expediente, String nomenclatura, Integer juzgadoId);

  @Query("""
          SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord(
                  d.id,
                  c.folio,
                  c.expediente,
                  c.tipoCarpeta,
                  d.ruta,
                 null
              )
          FROM Documento d
          JOIN d.carpeta c
          WHERE d.id = :documentoId
      """)
  BandejaRecepcionRecord findByDocumentoId(Integer documentoId);

  @Query("""
          SELECT new mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord(
              a.id, a.nombre, a.estado)
          FROM Anexo a
          JOIN a.documento d
          WHERE d.id = :documentoId
      """)
  List<AnexoBandejaRecepcionRecord> findAnexosByDocumentoId(Integer documentoId);

  @Transactional
  @Modifying
  @Query("UPDATE Carpeta c SET c.estatus = :estado WHERE c.id = :carpetaId")
  void actualizarEstatus(@Param("carpetaId") Integer carpetaId, @Param("estado") EstadoCarpeta estado);

  @Query("""
      SELECT COUNT(1)+1 FROM Carpeta c
      WHERE c.carpetaPadre.id=:carpetaId and c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA and c.expediente like %:clavePieza%
      """)
  Integer getNumeroPieza(Integer carpetaId, String clavePieza);

  @Query("""
      SELECT c.tipoJuicio.id
      FROM Carpeta c
      WHERE c.id = :carpetaId
      """)
  Integer findTipoJuicioIdByCarpetaId(@Param("carpetaId") Integer carpetaId);

  @Query("""
      SELECT new mx.gob.pjpuebla.trials.workflow.carpeta.records.PiezaRecordResponse(
          c.id, c.expediente, tp.tipo, c.estatus
      )
      FROM Documento d
      JOIN Carpeta c on c.carpetaPadre.id = d.carpeta.id
      JOIN c.tipoPieza tp
      WHERE d.id = :documentoId
      AND c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA
      AND c.estatus NOT IN (
          mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.CANCELADO,
          mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta.INTEGRADO)
      """)
  List<PiezaRecordResponse> findPiezasByDocumentoId(Integer documentoId);

  @Query("""
      SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta(
          c.id,
          c.expediente,
          null,
          c.tipoPieza,
          c.audit.fechaAlta,
          null,
          c.persona.id,
          c.tipoCarpeta,
          c.estatus
      )
      FROM Carpeta c
      JOIN c.tipoPieza tp
      WHERE c.carpetaPadre.id = :carpetaPadreId
      AND c.tipoCarpeta=mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA
      AND
          CASE WHEN :key IS NULL THEN 1
          WHEN c.expediente LIKE %:key% OR c.tipoPieza.tipo LIKE %:key% THEN 1
          ELSE 0 END = 1
      """)
  List<DocumentoDetalleCarpeta> findPiezasByCarpetaPadreId(String key, Integer carpetaPadreId);

  @Query("""
      SELECT c FROM Carpeta c
      WHERE c.juzgado IN :juzgados
      AND (
          :key IS NULL
          OR lower(c.expediente) LIKE %:key%
      )
      """)
  Page<Carpeta> findByJuzgado(@Param("juzgados") List<Juzgado> juzgados,
      @Param("key") String key,
      Pageable pageable);

  Optional<Carpeta> findByExpedienteAndJuzgadoIdAndEstatus(String expediente, Integer juzgadoId,
      EstadoCarpeta estado);

  @Query(value = """
      WITH jueces_penal AS (
          SELECT DISTINCT p.id AS juez_id, p.nombre
          FROM salas s
          JOIN persona p ON s.juez_id = p.id
          JOIN juzgado j ON s.juzgado_id = j.id
          JOIN materia m ON j.materia_id = m.id
          WHERE m.nombre = 'PENAL'
      ),
      ultimo_juez AS (
          SELECT p.id AS juez_id
          FROM audiencias a
          JOIN salas s ON a.sala_id = s.id
          JOIN persona p ON s.juez_id = p.id
          JOIN juzgado j ON s.juzgado_id = j.id
          JOIN materia m ON j.materia_id = m.id
          WHERE m.nombre = 'PENAL'
          ORDER BY a.id DESC
          LIMIT 1
      )
      SELECT p.*
      FROM jueces_penal p
      LEFT JOIN ultimo_juez u ON p.juez_id > u.juez_id
      ORDER BY p.juez_id ASC
      LIMIT 1
      """, nativeQuery = true)
  Persona findSiguienteJuezPenal();

  @Query(value = """
      SELECT
          CASE
              WHEN c.n_tipo_carpeta = 0 THEN 'D' || '.' ||  c.s_folio
              WHEN c.n_tipo_carpeta = 1 THEN 'E' || '.' ||  c.s_folio
              WHEN c.n_tipo_carpeta = 2 THEN 'A' || '.' ||  c.s_folio
              ELSE '' || c.s_folio
          END AS qr,
          c.s_expediente || CHR(10) || LOWER(juzgado.s_nombre)  expediente

      FROM tbl_carpetas c
      JOIN tbl_juzgados juzgado on juzgado.pn_id = c.fn_juzgado
      WHERE CAST(
              REGEXP_REPLACE(SPLIT_PART(c.s_expediente, '/', 1), '[^0-9]', '', 'g')
              AS INTEGER
            ) BETWEEN :expedienteMin AND :expedienteMax
        AND CAST(SPLIT_PART(c.s_expediente, '/', 2) AS INTEGER) = :year
        AND c.fn_juzgado = :juzgadoId
        AND c.n_tipo_carpeta = 0
        order by CAST(
              REGEXP_REPLACE(SPLIT_PART(c.s_expediente, '/', 1), '[^0-9]', '', 'g')
              AS INTEGER
            )
      """, nativeQuery = true)
  List<Object[]> findByExpMinAndExMaxAndYear(
      @Param("expedienteMin") Integer expedienteMin,
      @Param("expedienteMax") Integer expedienteMax,
      @Param("year") Integer year,
      @Param("juzgadoId") Integer juzgadoId);

  @Query("""
      SELECT new mx.gob.pjpuebla.trials.litigante.LitiganteExpedientesRecord(
          ca.id, ca.expediente, ma.nombre, '', '','', juz.nombre, 0L, se.nombre
      )
      FROM Carpeta ca
      JOIN ca.juzgado juz
      JOIN juz.sede se
      JOIN se.distrito di
      JOIN ca.tipoJuicio tj
      JOIN tj.materia ma
      WHERE ca.expediente = :expediente
      AND ma.id = :materiaId
      AND di.id = :distritoId
      """)
  List<LitiganteExpedientesRecord> getExpedientesByMateria(Integer materiaId, String expediente,
      Integer distritoId);

  @Query("""
      SELECT new mx.gob.pjpuebla.trials.litigante.responselitigante.PiezaRecord(
          ca.id, ca.expediente, tp.tipo
      )
      FROM Carpeta ca
      JOIN ca.carpetaPadre cap
      JOIN ca.tipoPieza tp
      WHERE cap.id = :carpetaPadreId
      AND ca.tipoCarpeta = :tipoCarpeta
      """)
  List<PiezaRecord> findPiezasByCarpetaId(Integer carpetaPadreId, TipoCarpeta tipoCarpeta);

  @Query("""
          SELECT DISTINCT
              CASE
                  WHEN d IS NOT NULL THEN d.tipoDocumento
                  WHEN c IS NOT NULL THEN c.tipoCarpeta
              END
          FROM Movimiento m
          LEFT JOIN m.documento d
          LEFT JOIN m.carpeta c
          WHERE m.estado = :bandeja
      """)
  List<String> findDistinctTipoEntradaByBandeja(@Param("bandeja") String bandeja);

  Optional<Carpeta> findByExpedienteAndJuzgado(String expediente, Juzgado juzgado);

  List<Carpeta> findByCarpetaPadre(Carpeta c);

  @Query(value = """
      SELECT ca.t_fecha_alta
      FROM trials.tbl_documentos doc
      JOIN trials.tbl_carpetas ca ON doc.fn_carpeta = ca.pn_id
      JOIN trials.tbl_tipo_juicio tj ON ca.fn_tipo_juicio = tj.pn_id
      JOIN trials.tbl_materias ma ON ma.pn_id = tj.fn_materia
      WHERE
         (
           doc.j_data->'tiposJuicios' IS NULL
           OR jsonb_typeof(doc.j_data->'tiposJuicios') <> 'array'
           OR EXISTS (
             SELECT 1
             FROM jsonb_array_elements(doc.j_data->'tiposJuicios') juicios
             WHERE (juicios->>'id')::int NOT IN (112,113)
           )
         )
      AND tj.pn_id NOT IN (:juiciosExcluidos)
      AND ma.pn_id IN (:materiasId)
      ORDER BY ca.t_fecha_alta
      LIMIT 1
      """, nativeQuery = true)
  LocalDateTime getDatesByMateria(List<Integer> materiasId, List<Integer> juiciosExcluidos);

  @Query(value = """
      SELECT ca.t_fecha_alta
      FROM tbl_documentos doc
      JOIN tbl_carpetas ca ON doc.fn_carpeta = ca.pn_id
      JOIN tbl_tipo_juicio tj ON ca.fn_tipo_juicio = tj.pn_id
      WHERE (
        jsonb_typeof(doc.j_data->'tiposJuicios') = 'array'
        AND EXISTS (
          SELECT 1
          FROM jsonb_array_elements(doc.j_data->'tiposJuicios') juicios
          WHERE (juicios->>'id')::int IN (:tipoJuicios)
        )
      ) OR tj.pn_id IN (:tipoJuicios)
      ORDER BY ca.t_fecha_alta
      LIMIT 1
      """, nativeQuery = true)
  LocalDateTime getDatesByTipoJuicio(List<Integer> tipoJuicios);

  @Query(value = QUERY_LIBRO_GOBIERNO, countQuery = COUNT_LIBRO_GOBIERNO)
  Page<LibroGobiernoRecord> findLibroGobierno(
      @Param("juzgados") List<Juzgado> juzgados,

      @Param("key") String key,
      @Param("expediente") String expediente,

      @Param("fechaFrom") LocalDateTime fechaFrom,
      @Param("fechaTo") LocalDateTime fechaTo,

      @Param("tipoJuicio") String tipoJuicio,
      @Param("actor") String actor,
      @Param("demandado") String demandado,
      @Param("cujus") String cujus,

      @Param("userId") Long userId,
      @Param("estadoAsignado") EstadoCarpeta estadoAsignado,
      Pageable pageable);

  @Query("""
          SELECT COUNT(c)
          FROM Carpeta c
          JOIN c.juzgado j
          JOIN j.materia m
          WHERE c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION AND m.nombre IN :materias
      """)
  Long countCarpetasPenales(@Param("materias") List<String> materias);
}
