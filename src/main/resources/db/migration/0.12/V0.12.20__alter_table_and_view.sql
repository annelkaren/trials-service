-- eliminamos vista ya que posgresql no permite modificar tamaño si las columnas se usan en una vista.
DROP VIEW IF EXISTS trials.reporte_divorcios;
DROP VIEW IF EXISTS trials.reporte_laboral;

-- ejecutamos alters
ALTER TABLE trials.tbl_personas_documentos 
ALTER COLUMN s_nombres TYPE VARCHAR(250);

ALTER TABLE trials.tbl_personas_documentos 
ALTER COLUMN s_apellido_paterno TYPE VARCHAR(250);

ALTER TABLE trials.tbl_personas_documentos 
ALTER COLUMN s_apellido_materno TYPE VARCHAR(250);

-- Recreamos la vista con los nuevos tamaños
CREATE OR REPLACE VIEW trials.reporte_divorcios AS
SELECT
  -- ===== DATOS DEL REGISTRO =====
  21::SMALLINT                                              AS "ENT_REG",
  tdom.s_municipio_id                                       AS "MPIO_REG",
  ca.s_expediente                                           AS "NuM_EXPDIV",
  6::SMALLINT                                               AS "CVE_EST",
   -- ===== DATOS DEL MATRIMONIO =====
  tcd.s_numero_acta_matrimonio                              AS "NuM_ACTAMAT",
  tcd.s_entidad                                             AS "ENT_REGMAT",
  tcd.s_municipio                                           AS "MPIO_REGMAT",
  tcd.s_localidad                                           AS "LOC_REGMAT",
  TO_CHAR(tcd.t_fecha_registro, 'DD')                       AS "DIA_MAT",
  TO_CHAR(tcd.t_fecha_registro, 'MM')                       AS "MES_MAT",
  TO_CHAR(tcd.t_fecha_registro, 'YYYY')                     AS "AÑO_MAT",
  -- ===== DATOS DEL DIVORCIO =====
  TO_CHAR(ca.t_fecha_alta, 'DD')                            AS "DIA_DEM",
  TO_CHAR(ca.t_fecha_alta, 'MM')                            AS "MES_DEM",
  TO_CHAR(ca.t_fecha_alta, 'YYYY')                          AS "AÑO_DEM",
  COALESCE(TO_CHAR(sen.fecha_sen, 'DD'), '')                AS "DIA_SEN",
  COALESCE(TO_CHAR(sen.fecha_sen, 'MM'), '')                AS "MES_SEN",
  COALESCE(TO_CHAR(sen.fecha_sen, 'YYYY'), '')              AS "AÑO_SEN",
  COALESCE(TO_CHAR(tcd.t_fecha_ejecutoria, 'DD'), '')       AS "DIA_EJEC",
  COALESCE(TO_CHAR(tcd.t_fecha_ejecutoria, 'MM'), '')       AS "MES_EJEC",
  COALESCE(TO_CHAR(tcd.t_fecha_ejecutoria, 'YYYY'), '')     AS "AÑO_EJEC",
  (
    CASE
      WHEN j.has_112 THEN CASE
        WHEN tpd1.s_sexo = 'Masculino' THEN 11
        WHEN tpd1.s_sexo = 'Femenino' THEN 12
        ELSE 1
      END
      ELSE 39
    END
  )::SMALLINT                                               AS "PERS_INICJUIC",
  '-PENDIENTE-'                                             AS "A FAVOR",
  28::SMALLINT                                              AS "CAUSA",
  CASE
    WHEN COALESCE(tcd.n_hijos, 0) = 0 THEN '99'
    ELSE TO_CHAR(tcd.n_hijos, 'FM00')
  END                                                       AS "TOTAL_HIJOS",
  CASE
    WHEN COALESCE(tcd.n_hijos_menores_edad, 0) = 0 THEN '99'
    ELSE TO_CHAR(tcd.n_hijos_menores_edad, 'FM00')
  END                                                       AS "MENORES_EDAD",
  '-PENDIENTE-'                                             AS "CUSTODIA",
  '-PENDIENTE-'                                             AS "PATRIA_POTESTAD",
  '-PENDIENTE-'                                             AS "PENSION_ALIM",
  -- ===== DATOS DEL DIVORCIANTE 1 =====
  CASE
    WHEN tpd1.s_sexo = 'Masculino' THEN '1'
    WHEN tpd1.s_sexo = 'Femenino' THEN '2'
    ELSE ''
  END                                                       AS "SEXO_DIV1",
  CASE
    WHEN tpd1.fn_nacionalidad = 73 THEN '1'
    WHEN tpd1.fn_nacionalidad <> 73 THEN '2'
    ELSE ''
  END                                                       AS "NAC_DIV1",
  tpd1.n_edad::SMALLINT                                     AS "EDAD_DIV1",
  TO_CHAR(tpd1.d_fecha_nacimiento, 'YYYY')                  AS "AÑONAC_DIV1",
  CASE
    WHEN tpd1.s_estado_civil = 'SOLTERO' THEN '1'
    WHEN tpd1.s_estado_civil = 'UNION_LIBRE' THEN '2'
    WHEN tpd1.s_estado_civil = 'DIVORCIADO' THEN '3'
    WHEN tpd1.s_estado_civil LIKE '%SEPARADO%' THEN '4'
    WHEN tpd1.s_estado_civil = 'VIUDO' THEN '5'
    ELSE ''
  END                                                       AS "EDOCONY_ANTMAT_DIV1",
  tdd1.s_estado_republica                                   AS "ENT_RESHAB_DIV1",
  tdd1.s_municipio                                          AS "MPIO_RESHAB_DIV1",
  tdd1.s_colonia                                            AS "LOC_RESHAB_DIV1",
  ted1.clave_reporte                                        AS "ESCOL_DIV1",
  '-PENDIENTE-'                                             AS "AQUESEDEDICA_DIV1",
  tpd1.n_posicion_trabajo                                   AS "POSICENELTRABAJO_DIV1",
  -- ===== DATOS DEL DIVORCIANTE 2 =====
  CASE
    WHEN tpd2.s_sexo = 'Masculino' THEN '1'
    WHEN tpd2.s_sexo = 'Femenino' THEN '2'
    ELSE ''
  END                                                       AS "SEXO_DIV2",
  CASE
    WHEN tpd2.fn_nacionalidad = 73 THEN '1'
    WHEN tpd2.fn_nacionalidad <> 73 THEN '2'
    ELSE ''
  END                                                       AS "NAC_DIV2",
  tpd2.n_edad::SMALLINT AS "EDAD_DIV2",
  TO_CHAR(tpd2.d_fecha_nacimiento, 'YYYY')                  AS "AÑONAC_DIV2",
  CASE
    WHEN tpd2.s_estado_civil = 'SOLTERO' THEN '1'
    WHEN tpd2.s_estado_civil = 'UNION_LIBRE' THEN '2'
    WHEN tpd2.s_estado_civil = 'DIVORCIADO' THEN '3'
    WHEN tpd2.s_estado_civil LIKE '%SEPARADO%' THEN '4'
    WHEN tpd2.s_estado_civil = 'VIUDO' THEN '5'
    ELSE ''
  END                                                       AS "EDOCONY_ANTMAT_DIV2",
  tdd2.s_estado_republica                                   AS "ENT_RESHAB_DIV2",
  tdd2.s_municipio                                          AS "MPIO_RESHAB_DIV2",
  tdd2.s_colonia                                            AS "LOC_RESHAB_DIV2",
  ted2.clave_reporte                                        AS "ESCOL_DIV2",
  '-PENDIENTE-'                                             AS "AQUESEDEDICA_DIV2",
  tpd2.n_posicion_trabajo                                   AS "POSICENELTRABAJO_DIV2",
  tcd.s_observaciones                                       AS "OBSERVACIONES"
FROM
  trials.tbl_documentos doc
  JOIN trials.tbl_carpetas ca ON ca.pn_id = doc.fn_carpeta
  JOIN trials.tbl_tipo_juicio tj ON tj.pn_id = ca.fn_tipo_juicio
  JOIN trials.tbl_carpeta_detalle tcd ON tcd.fn_carpeta = ca.pn_id
  JOIN trials.tbl_juzgados tjuz ON ca.fn_juzgado = tjuz.pn_id
  JOIN trials.tbl_sedes ts ON ts.pn_id = tjuz.fn_sede
  JOIN trials.tbl_domicilios tdom ON tdom.pn_id = ts.fn_domicilio
  LEFT JOIN LATERAL (
    SELECT
      *
    FROM
      trials.tbl_personas_documentos pd
    WHERE
      pd.fn_carpeta = ca.pn_id
      AND pd.fn_tipo_parte IN (1100, 5150)
    ORDER BY
      (pd.fn_tipo_parte = 1100) DESC
    LIMIT
      1
  ) d1 ON TRUE
  LEFT JOIN trials.tbl_persona_detalle tpd1 ON tpd1.fn_persona_documento = d1.pn_id
  LEFT JOIN trials.tbl_domicilios tdd1 ON tdd1.pn_id = tpd1.fn_domicilio
  LEFT JOIN trials.tbl_escolaridades ted1 ON ted1.pn_id = tpd1.fn_escolaridad
  LEFT JOIN LATERAL (
    SELECT
      *
    FROM
      trials.tbl_personas_documentos pd
    WHERE
      pd.fn_carpeta = ca.pn_id
      AND pd.fn_tipo_parte IN (1150, 5400)
    ORDER BY
      (pd.fn_tipo_parte = 1150) DESC
    LIMIT
      1
  ) d2 ON TRUE
  LEFT JOIN trials.tbl_persona_detalle tpd2 ON tpd2.fn_persona_documento = d2.pn_id
  LEFT JOIN trials.tbl_domicilios tdd2 ON tdd2.pn_id = tpd2.fn_domicilio
  LEFT JOIN trials.tbl_escolaridades ted2 ON ted2.pn_id = tpd2.fn_escolaridad
  CROSS JOIN LATERAL (
      SELECT
        bool_or(((ju ->> 'id') ~ '^\d+$') AND (ju ->> 'id')::int = 112)                       AS has_112,
        bool_or(((ju ->> 'id') ~ '^\d+$') AND ( (ju ->> 'id')::int = ANY (ARRAY[112,113]) ))  AS has_112_113
      FROM jsonb_array_elements(
             CASE
               WHEN jsonb_typeof(doc.j_data->'tiposJuicios') = 'array'
                 THEN doc.j_data->'tiposJuicios'
               WHEN doc.j_data ? 'tiposJuicios'
                 THEN jsonb_build_array(doc.j_data->'tiposJuicios')
               ELSE '[]'::jsonb
             END
           ) AS ju
    ) j
  LEFT JOIN LATERAL (
  SELECT td.t_fecha_alta AS fecha_sen
  FROM trials.tbl_documentos td
  JOIN trials.tbl_documento_detalle tdd ON tdd.fn_documento = td.pn_id
  WHERE td.fn_carpeta = ca.pn_id
    AND td.n_tipo_documento = 5
    AND tdd.n_tipo_sentencia = 0
  ORDER BY td.t_fecha_alta
  LIMIT 1
) sen ON TRUE
WHERE
  j.has_112_113;

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