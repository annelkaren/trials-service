--create new view
CREATE OR REPLACE VIEW trials.reporte_laboral AS
WITH base AS (
  SELECT
    j.s_nombre                                                                  AS "NOMBRE_ORGANO_JURISDICCIONAL",
    j.pn_id                                                                     AS "CLAVE_ORGANO_JURISDICCIONAL",
    c.s_expediente                                                              AS "CLAVE_EXPEDIENTE",
    TO_CHAR(c.t_fecha_alta, 'DD')                                               AS "DIA_DEM",
    TO_CHAR(c.t_fecha_alta, 'MM')                                               AS "MES_DEM",
    TO_CHAR(c.t_fecha_alta, 'YYYY')                                             AS "AÑO_DEM",
    c.pn_id                                                                     AS _carpeta_id,
    CASE tj.pn_id
      WHEN 305 THEN 'INDIVIDUAL'
      WHEN 306 THEN 'COLECTIVO'
      ELSE ''
    END                                                                         AS "TIPO_ASUNTO",
    CASE
      WHEN an_sel.n_estado = 1 THEN 'Si'
      ELSE 'No'
    END                                                                         AS "CONSTANCIA_NO_CONCILIACIÓN",
  mov_sel.s_estado                                                              AS "ESTATUS"
  FROM trials.tbl_carpetas  c
  JOIN trials.tbl_juzgados  j  ON c.fn_juzgado = j.pn_id
  JOIN trials.tbl_materias  m  ON j.fn_materia = m.pn_id
  LEFT JOIN trials.tbl_carpeta_detalle  cd ON c.pn_id = cd.fn_carpeta
  LEFT JOIN trials.tbl_tipo_juicio tj     ON tj.pn_id = cd.fn_tipo_juicio
  LEFT JOIN LATERAL (
    SELECT an.n_estado
    FROM trials.tbl_documentos doc
    JOIN trials.tbl_anexos an ON an.fn_documento = doc.pn_id
    WHERE doc.fn_carpeta = c.pn_id
      AND doc.n_tipo_documento IS NULL
      AND an.s_nombre = 'Constancia de no conciliación'
    ORDER BY an.pn_id DESC
    LIMIT 1
  ) an_sel ON TRUE
  LEFT JOIN LATERAL (
  SELECT mov.s_estado
  FROM trials.tbl_movimientos mov
  WHERE mov.fn_carpeta = c.pn_id
  ORDER BY mov.t_fecha_asignacion DESC, mov.pn_id DESC
  LIMIT 1
) mov_sel ON TRUE
  WHERE m.pn_id = 150
), act AS (
  SELECT
    pd.fn_carpeta                                 AS carpeta_id,
    pd.pn_id                                      AS id_actor,
    '-PENDIENTE-'::text                           AS actor,
    '-PENDIENTE-'::text                           AS defensa,
    tpd.s_sexo                                    AS sexo,
    tpd.n_edad                                    AS edad,
    '-PENDIENTE-'::text                           AS ocupacion,
    '-PENDIENTE-'::text                           AS nss,
    tpd.s_rfc                                     AS rfc,
    '-PENDIENTE-'::text                           AS jornada,
    '-PENDIENTE-'::text                           AS sindicato,
    '-PENDIENTE-'::text                           AS asociacion_sindical,
    '-PENDIENTE-'::text                           AS tipo_sindicato,
    '-PENDIENTE-'::text                           AS organizacion_obrera,
    '-PENDIENTE-'::text                           AS hombres_involucrados,
    '-PENDIENTE-'::text                           AS mujeres_involucradas,
    '-PENDIENTE-'::text                           AS no_identificado_involucrados,

    ROW_NUMBER() OVER (PARTITION BY pd.fn_carpeta ORDER BY pd.pn_id) AS rn,
    COUNT(*)    OVER (PARTITION BY pd.fn_carpeta)                    AS total_actores
  FROM trials.tbl_personas_documentos pd
  LEFT JOIN trials.tbl_persona_detalle tpd
         ON tpd.fn_persona_documento = pd.pn_id
  INNER JOIN trials.tbl_tipo_partes ttp
          ON ttp.pn_id = pd.fn_tipo_parte
  WHERE ttp.s_nombre = 'Actor'
),
  dem AS (
  SELECT
    pd.fn_carpeta                                 AS carpeta_id,
    pd.pn_id                                      AS id_demandado,
    '-PENDIENTE-'::text                           AS demandado,
    '-PENDIENTE-'::text                           AS defensa,
    pd.s_nombres                                 AS razon_social,
    pd.s_domicilio                               AS domicilio,
    ROW_NUMBER() OVER (PARTITION BY pd.fn_carpeta ORDER BY pd.pn_id) AS rn,
    COUNT(*)    OVER (PARTITION BY pd.fn_carpeta)                    AS total_actores
  FROM trials.tbl_personas_documentos pd
  INNER JOIN trials.tbl_tipo_partes ttp
          ON ttp.pn_id = pd.fn_tipo_parte
  WHERE ttp.s_nombre = 'Demandado'
),
proc_por_carpeta AS (
  SELECT
    c.pn_id AS carpeta_id,
    p.s_nombre                                                                  AS "TIPO_PROCEDIMIENTO"
  FROM trials.tbl_carpetas c
  LEFT JOIN LATERAL (
    SELECT p.s_nombre
    FROM trials.tbl_carpetas_rubros cr
    JOIN trials.tbl_rubros r         ON cr.fn_rubro        = r.pn_id
    JOIN trials.tbl_procedimientos p ON r.fn_procedimiento = p.pn_id
    WHERE cr.fn_carpeta = c.pn_id
    LIMIT 1
  ) p ON TRUE
),
aud AS (
  SELECT
    a.fn_carpeta AS carpeta_id,
    a.pn_id      AS id_audiencia,
    taud.s_nombre     AS tipo_audiencia,
    a.t_hora_inicio    AS hora_inicio,
    a.t_hora_fin       AS hora_fin,
    a.t_fecha_audiencia AS fecha_audiencia,
    ROW_NUMBER() OVER (
      PARTITION BY a.fn_carpeta
      ORDER BY a.t_fecha_audiencia NULLS LAST, a.pn_id
    ) AS rn,
    COUNT(*) OVER (PARTITION BY a.fn_carpeta) AS total_audiencias
  FROM trials.tbl_audiencias a
  INNER JOIN trials.tbl_tipo_audiencia taud ON a.fn_tipo_audiencia = taud.pn_id
)
SELECT
  b."NOMBRE_ORGANO_JURISDICCIONAL",
  b."CLAVE_ORGANO_JURISDICCIONAL",
  b."CLAVE_EXPEDIENTE",
  b."ESTATUS",
  COALESCE(ppc."TIPO_PROCEDIMIENTO", '')                                        AS "TIPO_PROCEDIMIENTO",
  b."DIA_DEM",
  b."MES_DEM",
  b."AÑO_DEM",
  COALESCE(MAX(a.total_audiencias), 0)::int                                     AS "TOTAL_AUDIENCIAS",
  MAX(CASE WHEN a.rn = 1 THEN a.id_audiencia END)                               AS "ID_AUDIENCIA_1",
  MAX(CASE WHEN a.rn = 1 THEN a.tipo_audiencia END)                             AS "TIPO_DE_AUDIENCIA_1",
  MAX(CASE WHEN a.rn = 1 THEN TO_CHAR(a.fecha_audiencia, 'DD/MM/YYYY') END)     AS "FECHA_AUDIENCIA_1",
  MAX(CASE WHEN a.rn = 1 THEN TO_CHAR(a.hora_inicio, 'HH24:MI') END)            AS "HORA_INICIO_1",
  MAX(CASE WHEN a.rn = 1 THEN TO_CHAR(a.hora_fin, 'HH24:MI') END)               AS "HORA_FIN_1",

  MAX(CASE WHEN a.rn = 2 THEN a.id_audiencia END)                               AS "ID_AUDIENCIA_2",
  MAX(CASE WHEN a.rn = 2 THEN a.tipo_audiencia END)                             AS "TIPO_DE_AUDIENCIA_2",
  MAX(CASE WHEN a.rn = 2 THEN TO_CHAR(a.fecha_audiencia, 'DD/MM/YYYY') END)     AS "FECHA_AUDIENCIA_2",
  MAX(CASE WHEN a.rn = 2 THEN TO_CHAR(a.hora_inicio, 'HH24:MI') END)            AS "HORA_INICIO_2",
  MAX(CASE WHEN a.rn = 2 THEN TO_CHAR(a.hora_fin, 'HH24:MI') END)               AS "HORA_FIN_2",

  MAX(CASE WHEN a.rn = 3 THEN a.id_audiencia END)                               AS "ID_AUDIENCIA_3",
  MAX(CASE WHEN a.rn = 3 THEN a.tipo_audiencia END)                             AS "TIPO_DE_AUDIENCIA_3",
  MAX(CASE WHEN a.rn = 3 THEN TO_CHAR(a.fecha_audiencia, 'DD/MM/YYYY') END)     AS "FECHA_AUDIENCIA_3",
  MAX(CASE WHEN a.rn = 3 THEN TO_CHAR(a.hora_inicio, 'HH24:MI') END)            AS "HORA_INICIO_3",
  MAX(CASE WHEN a.rn = 3 THEN TO_CHAR(a.hora_fin, 'HH24:MI') END)               AS "HORA_FIN_3",
  b."TIPO_ASUNTO",
  '-PENDIENTE-' AS "NATURALEZA_CONFLICTO",
  '-PENDIENTE-' AS "CONTRATO_ESCRITO",
  '-PENDIENTE-' AS "TIPO_CONTRATO",
  '-PENDIENTE-' AS "RAMA_MATERIA_INVOLUCRADA",
  '-PENDIENTE-' AS "ENTIDAD_CONFLICTO",
  '-PENDIENTE-' AS "MUNICIPIO_CONFLICTO",
  '-PENDIENTE-' AS "OUTSOURSING",
  '-PENDIENTE-' AS "MOTIVO_CONFLICTO",
  '-PENDIENTE-' AS "CIRCUNSTANCIAS_CONFLICTO",
  '-PENDIENTE-' AS "CONCEPTO_RECLAMADO",
  '-PENDIENTE-' AS "TIPO_PRESTACIONES",
  '-PENDIENTE-' AS "INCOMPETENCIA",
  '-PENDIENTE-' AS "TIPO DE INCOMPETENCIA",
  b."CONSTANCIA_NO_CONCILIACIÓN",
  '-PENDIENTE-' AS "CLAVE_CONSTANCIA",
  '-PENDIENTE-' AS "VINCULADO_CONCILIACION_PREJUDICIAL",
  '-PENDIENTE-' AS "PREVENCION_DEMANDA",
  '-PENDIENTE-' AS "DESHAGO_PREVENCION_DEMANDA",
  '-PENDIENTE-' AS "CAUSAS_NO_ADMISIÓN_DEMANDA",
  '-PENDIENTE-' AS "FECHA_ADMISIÓN_DEMANDA",
  COALESCE(MAX(act.total_actores), 0)::int AS "TOTAL_ACTORES",
  -- ====== ACTOR 1 ======
MAX(CASE WHEN act.rn = 1 THEN act.id_actor                END) AS "ID_ACTOR_1",
MAX(CASE WHEN act.rn = 1 THEN act.actor                   END) AS "ACTOR_1",
MAX(CASE WHEN act.rn = 1 THEN act.defensa                 END) AS "DEFENSA_1",
MAX(CASE WHEN act.rn = 1 THEN act.sexo                    END) AS "SEXO_1",
MAX(CASE WHEN act.rn = 1 THEN act.edad                    END) AS "EDAD_1",
MAX(CASE WHEN act.rn = 1 THEN act.ocupacion               END) AS "OCUPACION_1",
MAX(CASE WHEN act.rn = 1 THEN act.nss                     END) AS "NSS_1",
MAX(CASE WHEN act.rn = 1 THEN act.rfc                     END) AS "RFC_1",
MAX(CASE WHEN act.rn = 1 THEN act.jornada                 END) AS "JORNADA_1",
MAX(CASE WHEN act.rn = 1 THEN act.sindicato               END) AS "SINDICATO_1",
MAX(CASE WHEN act.rn = 1 THEN act.asociacion_sindical     END) AS "ASOCIACION_SINDICAL_1",
MAX(CASE WHEN act.rn = 1 THEN act.tipo_sindicato          END) AS "TIPO_SINDICATO_1",
MAX(CASE WHEN act.rn = 1 THEN act.organizacion_obrera     END) AS "ORGANIZACION_OBRERA_1",
MAX(CASE WHEN act.rn = 1 THEN act.hombres_involucrados    END) AS "HOMBRES_INVOLUCRADOS_1",
MAX(CASE WHEN act.rn = 1 THEN act.mujeres_involucradas    END) AS "MUJERES_INVOLUCRADAS_1",
MAX(CASE WHEN act.rn = 1 THEN act.no_identificado_involucrados END) AS "NO_IDENTIFICADO_INVOLUCRADOS_1",

-- ====== ACTOR 2 ======
MAX(CASE WHEN act.rn = 2 THEN act.id_actor                END) AS "ID_ACTOR_2",
MAX(CASE WHEN act.rn = 2 THEN act.actor                   END) AS "ACTOR_2",
MAX(CASE WHEN act.rn = 2 THEN act.defensa                 END) AS "DEFENSA_2",
MAX(CASE WHEN act.rn = 2 THEN act.sexo                    END) AS "SEXO_2",
MAX(CASE WHEN act.rn = 2 THEN act.edad                    END) AS "EDAD_2",
MAX(CASE WHEN act.rn = 2 THEN act.ocupacion               END) AS "OCUPACION_2",
MAX(CASE WHEN act.rn = 2 THEN act.nss                     END) AS "NSS_2",
MAX(CASE WHEN act.rn = 2 THEN act.rfc                     END) AS "RFC_2",
MAX(CASE WHEN act.rn = 2 THEN act.jornada                 END) AS "JORNADA_2",
MAX(CASE WHEN act.rn = 2 THEN act.sindicato               END) AS "SINDICATO_2",
MAX(CASE WHEN act.rn = 2 THEN act.asociacion_sindical     END) AS "ASOCIACION_SINDICAL_2",
MAX(CASE WHEN act.rn = 2 THEN act.tipo_sindicato          END) AS "TIPO_SINDICATO_2",
MAX(CASE WHEN act.rn = 2 THEN act.organizacion_obrera     END) AS "ORGANIZACION_OBRERA_2",
MAX(CASE WHEN act.rn = 2 THEN act.hombres_involucrados    END) AS "HOMBRES_INVOLUCRADOS_2",
MAX(CASE WHEN act.rn = 2 THEN act.mujeres_involucradas    END) AS "MUJERES_INVOLUCRADAS_2",
MAX(CASE WHEN act.rn = 2 THEN act.no_identificado_involucrados END) AS "NO_IDENTIFICADO_INVOLUCRADOS_2",

  COALESCE(MAX(act.total_actores), 0)::int AS "TOTAL_DEMANDADOS",
  -- ====== DEMANDADO 1 ======
MAX(CASE WHEN dem.rn = 1 THEN dem.id_demandado             END) AS "ID_DEMANDADO",
MAX(CASE WHEN dem.rn = 1 THEN dem.demandado                END) AS "DEMANDADO",
MAX(CASE WHEN dem.rn = 3 THEN dem.defensa                  END) AS "DEFENSA",
MAX(CASE WHEN dem.rn = 3 THEN dem.razon_social             END) AS "DEN_RAZON_SOCIAL",
MAX(CASE WHEN dem.rn = 3 THEN dem.domicilio                END) AS "DOMICILIO",
  '-PENDIENTE-' AS "AUDIENCIA_PRELIMINAR",
  '-PENDIENTE-' AS "FECHA_AUDIENCIA_PRELIMINAR",
  '-PENDIENTE-' AS "AUDIENCIA_JUICIO",
  '-PENDIENTE-' AS "FECHA_ULTIMO_ACTO_PROCESAL",
  '-PENDIENTE-' AS "FASE_SOLUCIÓN_EXPEDIENTE"
FROM base b
LEFT JOIN proc_por_carpeta ppc ON ppc.carpeta_id = b._carpeta_id
LEFT JOIN aud a                ON a.carpeta_id   = b._carpeta_id AND a.rn <= 10
LEFT JOIN act act              ON act.carpeta_id = b._carpeta_id AND act.rn <= 10
LEFT JOIN dem dem              ON dem.carpeta_id = b._carpeta_id AND dem.rn <= 10
GROUP BY
  b."CLAVE_ORGANO_JURISDICCIONAL",
  b."NOMBRE_ORGANO_JURISDICCIONAL",
  b."CLAVE_EXPEDIENTE",
  b."ESTATUS",
  ppc."TIPO_PROCEDIMIENTO",
  b."DIA_DEM",
  b."MES_DEM",
  b."AÑO_DEM",
  b."TIPO_ASUNTO",
  b."CONSTANCIA_NO_CONCILIACIÓN"
ORDER BY
  b."NOMBRE_ORGANO_JURISDICCIONAL",
  b."CLAVE_EXPEDIENTE";