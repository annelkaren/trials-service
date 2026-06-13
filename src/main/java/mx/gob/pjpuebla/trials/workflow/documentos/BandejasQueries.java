package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.Data;

@Data
public final class BandejasQueries {

    /**
     * Expresión JPQL que resuelve el tipo de entrada como string legible.
     * Usa aliases: d (Documento), c (Carpeta directa del movimiento), cd (Carpeta del documento).
     * Agregar nuevos tipos aquí los propaga automáticamente a SELECT, filtros y ordenamiento.
     */
    static final String TIPO_ENTRADA_EXPR =
        "CASE" +
        " WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN 'PROMOCION'" +
        " WHEN c IS NOT NULL THEN CASE c.tipoCarpeta" +
        " WHEN 0 THEN 'DEMANDA' WHEN 1 THEN 'EXHORTO' WHEN 2 THEN 'APELACION'" +
        " WHEN 3 THEN 'DESPACHO' WHEN 4 THEN 'APELACION_MUNICIPAL' WHEN 5 THEN 'AMPARO'" +
        " WHEN 6 THEN 'CARTA_ROGATORIA' WHEN 7 THEN 'COOPERACION_JUDICIAL_E_INTERNACIONAL'" +
        " WHEN 8 THEN 'OFICIO' WHEN 9 THEN 'PIEZA' ELSE '' END" +
        " ELSE CASE cd.tipoCarpeta" +
        " WHEN 0 THEN 'DEMANDA' WHEN 1 THEN 'EXHORTO' WHEN 2 THEN 'APELACION'" +
        " WHEN 3 THEN 'DESPACHO' WHEN 4 THEN 'APELACION_MUNICIPAL' WHEN 5 THEN 'AMPARO'" +
        " WHEN 6 THEN 'CARTA_ROGATORIA' WHEN 7 THEN 'COOPERACION_JUDICIAL_E_INTERNACIONAL'" +
        " WHEN 8 THEN 'OFICIO' WHEN 9 THEN 'PIEZA' ELSE '' END" +
        " END";

    public static final String QUERY_BANDEJA_ENTRADA = """
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaEntradaRecord(
              m.id,
              COALESCE(d.id, d2.id),
              COALESCE(d.folio, c.folio, cd.folio, ''),
              COALESCE(c.expediente, cd.expediente, ''),
              COALESCE(mjc.nombre, mjcd.nombre, ''),

              """ + TIPO_ENTRADA_EXPR + """
              ,

              COALESCE(jc.nombre, jcd.nombre, ''),
              COALESCE(d.audit.fechaAlta, d2.audit.fechaAlta),
              COALESCE(c.selloEstatus, cd.selloEstatus),

              CASE
                WHEN COALESCE(d.tipoDocumento, d2.tipoDocumento) IS NOT NULL THEN COALESCE(d.estatus, d2.estatus)
                ELSE COALESCE(c.estatus, cd.estatus)
              END,

              CASE WHEN COALESCE(d.ruta, d2.ruta) IS NOT NULL THEN true ELSE false END,
              m.motivo
            )
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN jc.materia mjc

            LEFT JOIN m.documento d
            LEFT JOIN d.carpeta cd
            LEFT JOIN cd.juzgado jcd
            LEFT JOIN jcd.materia mjcd

            LEFT JOIN m.juzgado j
            LEFT JOIN m.oficialia o

            LEFT JOIN Documento d2
              ON d2.carpeta = c
             AND (
                  (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA AND d2.tipoDocumento IS NULL)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.EXHORTO)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.APELACION)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION)
             )

            WHERE (
                 (c IS NOT NULL AND c.estatus IN (0,14))
              OR (d IS NOT NULL AND d.estatus IN (0,14))
            )
            AND m.fechaAsignacion = (
              SELECT MAX(m2.fechaAsignacion)
              FROM Movimiento m2
              WHERE (
                   (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
              )
            )
            AND m.id = (
              SELECT MAX(m2b.id)
              FROM Movimiento m2b
              WHERE (
                   (m.carpeta.id IS NOT NULL AND m2b.carpeta.id = m.carpeta.id)
                OR (m.documento.id IS NOT NULL AND m2b.documento.id = m.documento.id)
              )
              AND m2b.fechaAsignacion = m.fechaAsignacion
            )

            AND m.estado IN ('CAPTURA','EDICION','DEVUELTO_A_OFICIALIA')
            AND (o.id = :oficialiaId OR j.id = :juzgadoId)

            AND (
              (
                COALESCE(:cmdLetra, '') = '' AND (
                  COALESCE(:key, '') = '' OR
                  LOWER(COALESCE(d.folio, c.folio, cd.folio, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(mjc.nombre, mjcd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(jc.nombre, jcd.nombre, j.nombre, o.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(""" + TIPO_ENTRADA_EXPR + """
                  ) LIKE CONCAT('%', COALESCE(:key,''), '%')
                )
              )
              OR
              (
                COALESCE(:cmdLetra, '') <> '' AND COALESCE(:cmdFolio, '') <> '' AND (
                  (
                    UPPER(COALESCE(:cmdLetra,'')) = 'P'
                    AND d IS NOT NULL
                    AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                    AND LOWER(d.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'D'
                    AND (
                         (c IS NOT NULL AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA)
                      OR (cd IS NOT NULL AND cd.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA)
                    )
                    AND LOWER(COALESCE(c.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'A'
                    AND (
                         (c IS NOT NULL AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION)
                      OR (cd IS NOT NULL AND cd.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION)
                    )
                    AND LOWER(COALESCE(c.folio, cd.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'E'
                    AND (
                         (c IS NOT NULL AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO)
                      OR (cd IS NOT NULL AND cd.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO)
                    )
                    AND LOWER(COALESCE(c.folio, cd.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                )
              )
            )


            AND (
              COALESCE(:folio, '') = '' OR
              LOWER(COALESCE(d.folio, c.folio, cd.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:folio,'')), '%')
            )

            AND (
              COALESCE(:expediente, '') = '' OR
              LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
            )

            AND (
              COALESCE(:materia, '') = '' OR
              LOWER(COALESCE(mjc.nombre, mjcd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:materia,'')), '%')
            )

            AND (
              COALESCE(:organoJurisdiccional, '') = '' OR
              LOWER(COALESCE(jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:organoJurisdiccional,'')), '%')
            )

            AND (
              COALESCE(:tipoEntrada, '') = '' OR
              UPPER(""" + TIPO_ENTRADA_EXPR + """
              ) LIKE CONCAT('%', UPPER(COALESCE(:tipoEntrada,'')), '%')
            )

            AND COALESCE(d.audit.fechaAlta, d2.audit.fechaAlta, m.fechaAsignacion) >= :fechaFrom
            AND COALESCE(d.audit.fechaAlta, d2.audit.fechaAlta, m.fechaAsignacion) < :fechaTo
            """;

    public static final String QUERY_BANDEJA_SALIDA = """
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSalidaResponseRecord(
              m.id,
              COALESCE(c.id, doc_carpeta.id),
              COALESCE(c.folio, doc.folio, ''),
              COALESCE(c.expediente, doc_carpeta.expediente, ''),
              COALESCE(jc.id, jd.id),
              COALESCE(jc.nombre, jd.nombre, ''),
              COALESCE(matc.nombre, matd.nombre, ''),

              CASE
                WHEN doc IS NOT NULL AND doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN 'PROMOCION'
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
                  CASE doc_carpeta.tipoCarpeta
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
              WHERE (
                   (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
              )
            )

            AND m.estado = 'SALIDA'
            AND (m.oficialia.id = :oficialiaId OR m.juzgado.id = :juzgadoId)


            AND (
              (
                COALESCE(:cmdLetra,'') = '' AND (
                  COALESCE(:key,'') = '' OR
                  LOWER(COALESCE(jc.nombre, jd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(c.folio, doc.folio, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(c.expediente, doc_carpeta.expediente, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(
                    CASE
                      WHEN doc IS NOT NULL AND doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN 'PROMOCION'
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
                        CASE doc_carpeta.tipoCarpeta
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
                    END
                  ) LIKE CONCAT('%', COALESCE(:key,''), '%')
                )
              )
              OR
              (
                COALESCE(:cmdLetra,'') <> '' AND COALESCE(:cmdFolio,'') <> '' AND (
                  (
                    UPPER(COALESCE(:cmdLetra,'')) = 'P'
                    AND doc IS NOT NULL
                    AND doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                    AND LOWER(doc.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'D'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'A'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'E'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                )
              )
            )


            AND (
              COALESCE(:folio,'') = '' OR
              LOWER(COALESCE(c.folio, doc.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:folio,'')), '%')
            )
            AND (
              COALESCE(:expediente,'') = '' OR
              LOWER(COALESCE(c.expediente, doc_carpeta.expediente, '')) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
            )
            AND (
              COALESCE(:materia,'') = '' OR
              LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:materia,'')), '%')
            )
            AND (
              COALESCE(:organoJurisdiccional,'') = '' OR
              LOWER(COALESCE(jc.nombre, jd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:organoJurisdiccional,'')), '%')
            )
            AND (
              COALESCE(:tipoEntrada,'') = '' OR
              UPPER(
                CASE
                  WHEN doc IS NOT NULL AND doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN 'PROMOCION'
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
                    CASE doc_carpeta.tipoCarpeta
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
                END
              ) LIKE CONCAT('%', UPPER(COALESCE(:tipoEntrada,'')), '%')
            )

            AND COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta)
                >= COALESCE(:fechaFrom, COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta))

            AND COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta)
                <= COALESCE(:fechaTo,   COALESCE(c.audit.fechaAlta, doc_carpeta.audit.fechaAlta))
            """;

    public static final String QUERY_BANDEJA_HISTORIAL = """
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaHistorialRecord(
              m.id,
              COALESCE(c.folio, d.folio, cd.folio, ''),
              COALESCE(c.expediente, cd.expediente, ''),
              COALESCE(matc.nombre, matd.nombre, ''),
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

              m.fechaAsignacion,
              CASE
                WHEN COALESCE(d.tipoDocumento, d2.tipoDocumento) IS NOT NULL THEN COALESCE(d.estatus, d2.estatus)
                ELSE COALESCE(c.estatus, cd.estatus)
              END,

              COALESCE(jc.nombre, jcd.nombre, ''),

              CASE
                WHEN m.estado IN ('CAPTURA','EDICION','DEVUELTO_A_OFICIALIA','SALIDA') THEN m.estado
                ELSE 'En juzgado'
              END
            )
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN jc.materia matc

            LEFT JOIN m.documento d
            LEFT JOIN d.carpeta cd
            LEFT JOIN cd.juzgado jcd
            LEFT JOIN jcd.materia matd

            LEFT JOIN m.juzgado j
            LEFT JOIN m.oficialia o

            LEFT JOIN Documento d2
              ON d2.carpeta = c
             AND (
                  (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA AND d2.tipoDocumento IS NULL)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.EXHORTO)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.APELACION)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION)
             )

            WHERE (
              (:oficialiaId IS null AND :juzgadoId IS null)
              OR (:oficialiaId IS NOT null AND o.id = :oficialiaId)
              OR (:juzgadoId IS NOT null AND j.id = :juzgadoId)
            )

            AND (
              (
                COALESCE(:cmdLetra,'') = '' AND (
                  COALESCE(:key,'') = '' OR
                  LOWER(COALESCE(c.folio, d.folio, cd.folio, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(
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
                    END
                  ) LIKE CONCAT('%', COALESCE(:key,''), '%')
                )
              )
              OR
              (
                COALESCE(:cmdLetra,'') <> '' AND COALESCE(:cmdFolio,'') <> '' AND (
                  (
                    UPPER(COALESCE(:cmdLetra,'')) = 'P'
                    AND d IS NOT NULL
                    AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                    AND LOWER(d.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'D'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'A'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'E'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                )
              )
            )

            AND (
              COALESCE(:folio,'') = '' OR
              LOWER(COALESCE(c.folio, d.folio, cd.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:folio,'')), '%')
            )
            AND (
              COALESCE(:expediente,'') = '' OR
              LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
            )
            AND (
              COALESCE(:materia,'') = '' OR
              LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:materia,'')), '%')
            )
            AND (
              COALESCE(:organoJurisdiccional,'') = '' OR
              LOWER(COALESCE(jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:organoJurisdiccional,'')), '%')
            )
            AND (
              COALESCE(:tipoEntrada,'') = '' OR
              UPPER(
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
                END
              ) LIKE CONCAT('%', UPPER(COALESCE(:tipoEntrada,'')), '%')
            )

            AND m.fechaAsignacion >= COALESCE(:fechaFrom, m.fechaAsignacion)
            AND m.fechaAsignacion <= COALESCE(:fechaTo,   m.fechaAsignacion)
            
            """;

    public static final String QUERY_BANDEJA_HISTORIAL_ARCHIVO_JUDICIAL = """
            SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.BandejaHistorialRecord(
              m.id,
              COALESCE(c.folio, d.folio, cd.folio, ''),
              COALESCE(c.expediente, cd.expediente, ''),
              COALESCE(matc.nombre, matd.nombre, ''),
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

              m.fechaAsignacion,
              CASE
                WHEN COALESCE(d.tipoDocumento, d2.tipoDocumento) IS NOT NULL THEN COALESCE(d.estatus, d2.estatus)
                ELSE COALESCE(c.estatus, cd.estatus)
              END,

              COALESCE(jc.nombre, jcd.nombre, ''),
              m.estado
            )
            FROM Movimiento m
            LEFT JOIN m.carpeta c
            LEFT JOIN c.juzgado jc
            LEFT JOIN jc.materia matc

            LEFT JOIN m.documento d
            LEFT JOIN d.carpeta cd
            LEFT JOIN cd.juzgado jcd
            LEFT JOIN jcd.materia matd

            LEFT JOIN m.juzgado j
            LEFT JOIN m.oficialia o

            LEFT JOIN Documento d2
              ON d2.carpeta = c
             AND (
                  (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA AND d2.tipoDocumento IS NULL)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.EXHORTO)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.APELACION)
               OR (c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.PIEZA AND d2.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION)
             )

            WHERE (
              (:oficialiaId IS null AND :juzgadoId IS null)
              OR (:oficialiaId IS NOT null AND o.id = :oficialiaId)
              OR (:juzgadoId IS NOT null AND j.id = :juzgadoId)
            )

            AND (
              (
                COALESCE(:cmdLetra,'') = '' AND (
                  COALESCE(:key,'') = '' OR
                  LOWER(COALESCE(c.folio, d.folio, cd.folio, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(COALESCE(jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                  OR LOWER(
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
                    END
                  ) LIKE CONCAT('%', COALESCE(:key,''), '%')
                )
              )
              OR
              (
                COALESCE(:cmdLetra,'') <> '' AND COALESCE(:cmdFolio,'') <> '' AND (
                  (
                    UPPER(COALESCE(:cmdLetra,'')) = 'P'
                    AND d IS NOT NULL
                    AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                    AND LOWER(d.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'D'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'A'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                  OR (
                    UPPER(COALESCE(:cmdLetra,'')) = 'E'
                    AND c IS NOT NULL
                    AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO
                    AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                  )
                )
              )
            )

            AND (
              COALESCE(:folio,'') = '' OR
              LOWER(COALESCE(c.folio, d.folio, cd.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:folio,'')), '%')
            )
            AND (
              COALESCE(:expediente,'') = '' OR
              LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
            )
            AND (
              COALESCE(:materia,'') = '' OR
              LOWER(COALESCE(matc.nombre, matd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:materia,'')), '%')
            )
            AND (
              COALESCE(:organoJurisdiccional,'') = '' OR
              LOWER(COALESCE(jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', LOWER(COALESCE(:organoJurisdiccional,'')), '%')
            )
            AND (
              COALESCE(:tipoEntrada,'') = '' OR
              UPPER(
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
                END
              ) LIKE CONCAT('%', UPPER(COALESCE(:tipoEntrada,'')), '%')
            )

            AND m.fechaAsignacion >= COALESCE(:fechaFrom, m.fechaAsignacion)
            AND m.fechaAsignacion <= COALESCE(:fechaTo,   m.fechaAsignacion)
            """;

    public static final String QUERY_BANDEJA_RECEPCION = """
                  SELECT new mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord(
                    m.id,

                    CASE WHEN c IS NOT NULL THEN c.id ELSE cd.id END,

                    CASE WHEN d IS NOT NULL THEN d.id ELSE NULL END,

                    CASE
                      WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN d.folio
                      WHEN c IS NOT NULL THEN c.folio
                      ELSE cd.folio
                    END,

                    CASE WHEN c IS NOT NULL THEN c.expediente ELSE cd.expediente END,

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

                    CONCAT(p.nombre,' ',p.apellidoPaterno,' ',COALESCE(p.apellidoMaterno,'')),

                    CASE
                      WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.nombre
                      WHEN c IS NOT NULL THEN cc.nombre
                      ELSE cdc.nombre
                    END,

                    m.fechaAsignacion,

                    CASE
                      WHEN :isOficialMayor = false THEN true
                      WHEN :userJuzgadoNombre IS NOT NULL AND LOWER(COALESCE(o.nombre, j.nombre, '')) = :userJuzgadoNombre THEN true
                      WHEN :userOficialiaNombre IS NOT NULL AND LOWER(COALESCE(o.nombre, j.nombre, '')) = :userOficialiaNombre THEN true
                      ELSE false
                    END,

                    CASE WHEN c IS NOT NULL THEN c.prioridad ELSE cd.prioridad END,

                    CASE WHEN c IS NOT NULL THEN c.horas ELSE cd.horas END,

                    CASE
                      WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.id
                      WHEN c IS NOT NULL THEN cc.id
                      ELSE cdc.id
                    END,

                    CASE
                      WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN
                        CAST(function('jsonb_extract_path_text', d.data, 'tipoPromocion') as String)
                      ELSE ''
                    END,
                    
                    CASE
                      WHEN c IS NOT NULL THEN jc.materia.nombre
                      ELSE jcd.materia.nombre
                    END
                  )
                  FROM Movimiento m
                  LEFT JOIN m.carpeta c
                  LEFT JOIN c.juzgado jc
                  LEFT JOIN jc.materia jcm
                  LEFT JOIN c.concepto cc
                  LEFT JOIN m.documento d
                  LEFT JOIN d.concepto dc
                  LEFT JOIN d.carpeta cd
                  LEFT JOIN cd.juzgado jcd
                  LEFT JOIN jcd.materia jcdm
                  LEFT JOIN cd.concepto cdc
                  JOIN m.persona p
                  LEFT JOIN m.juzgado j
                  LEFT JOIN m.oficialia o
                  WHERE (
                    (c IS NOT NULL AND c.estatus IN :estadoList)
                    OR (d IS NOT NULL AND d.estatus IN :estadoList)
                  )

                  AND m.fechaAsignacion = (
                    SELECT MAX(m2.fechaAsignacion)
                    FROM Movimiento m2
                    WHERE (
                      (m.carpeta.id IS NOT NULL AND m2.carpeta.id = m.carpeta.id)
                      OR (m.documento.id IS NOT NULL AND m2.documento.id = m.documento.id)
                    )
                    AND (
                      :isOficialMayor = false
                      OR (m2.estado <> 'TURNADO' OR (m2.estado = 'TURNADO' AND m2.destino = :personaId))
                    )
                  )

                  AND (
                    (:isOficialMayor = false AND m.estado = :motivoSingle AND m.destino = :personaId)
                    OR (:isOficialMayor = true  AND m.estado IN (:motivosList))
                  )

                  AND (
                    (c IS NOT NULL AND jc.id = :juzgadoId)
                    OR (d IS NOT NULL AND jcd.id = :juzgadoId)
                  )

                  AND (
                    (
                      COALESCE(:cmdLetra, '') = '' AND (
                        COALESCE(:key, '') = '' OR
                        LOWER(COALESCE(c.folio, cd.folio, d.folio, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(COALESCE(j.nombre, o.nombre, jc.nombre, jcd.nombre, '')) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(p.nombre) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(p.apellidoPaterno) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(CONCAT(p.nombre,' ',p.apellidoPaterno,' ',COALESCE(p.apellidoMaterno,''))) LIKE CONCAT('%', COALESCE(:key,''), '%')
                        OR LOWER(
                          CASE
                            WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.nombre
                            WHEN c IS NOT NULL THEN cc.nombre
                            ELSE cdc.nombre
                          END
                        ) LIKE CONCAT('%', COALESCE(:key,''), '%')
                      )
                    )
                    OR
                    (
                      COALESCE(:cmdLetra, '') <> '' AND COALESCE(:cmdFolio, '') <> '' AND (
                        (
                          UPPER(COALESCE(:cmdLetra,'')) = 'P'
                          AND d IS NOT NULL
                          AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION
                          AND LOWER(d.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                        )
                        OR (
                          UPPER(COALESCE(:cmdLetra,'')) = 'D'
                          AND c IS NOT NULL
                          AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.DEMANDA
                          AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                        )
                        OR (
                          UPPER(COALESCE(:cmdLetra,'')) = 'A'
                          AND c IS NOT NULL
                          AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.APELACION
                          AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                        )
                        OR (
                          UPPER(COALESCE(:cmdLetra,'')) = 'E'
                          AND c IS NOT NULL
                          AND c.tipoCarpeta = mx.gob.pjpuebla.trials.util.enums.TipoCarpeta.EXHORTO
                          AND LOWER(c.folio) LIKE CONCAT('%', LOWER(COALESCE(:cmdFolio,'')), '%')
                        )
                      )
                    )
                  )

                  AND (
                    COALESCE(:folio, '') = '' OR
                    LOWER(COALESCE(c.folio, cd.folio, d.folio, '')) LIKE CONCAT('%', LOWER(COALESCE(:folio,'')), '%')
                  )

                  AND (
                    COALESCE(:expediente, '') = '' OR
                    LOWER(COALESCE(c.expediente, cd.expediente, '')) LIKE CONCAT('%', LOWER(COALESCE(:expediente,'')), '%')
                  )

                  AND (
                    COALESCE(:tipoEntrada, '') = '' OR
                    UPPER(
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
                      END
                    ) = UPPER(COALESCE(:tipoEntrada,''))
                  )

                  AND (
                    COALESCE(:origen, '') = '' OR
                    LOWER(CONCAT(p.nombre,' ',p.apellidoPaterno,' ',COALESCE(p.apellidoMaterno,'')))
                      LIKE CONCAT('%', LOWER(COALESCE(:origen,'')), '%')
                  )

                  AND (
                    COALESCE(:motivoTurnado, '') = '' OR
                    LOWER(
                      CASE
                        WHEN d IS NOT NULL AND d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.PROMOCION THEN dc.nombre
                        WHEN c IS NOT NULL THEN cc.nombre
                        ELSE cdc.nombre
                      END
                    ) LIKE CONCAT('%', LOWER(COALESCE(:motivoTurnado,'')), '%')
                  )

                  AND m.fechaAsignacion >= COALESCE(:fechaFrom, m.fechaAsignacion)
                  AND m.fechaAsignacion <= COALESCE(:fechaTo,   m.fechaAsignacion)
            """;

}
