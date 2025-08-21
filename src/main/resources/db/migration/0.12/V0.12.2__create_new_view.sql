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
  ''                                                        AS "DIA_EJEC",
  ''                                                        AS "MES_EJEC",
  ''                                                        AS "AÑO_EJEC",
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
  ''                                                        AS "A FAVOR",
  28::SMALLINT                                              AS "CAUSA",
  CASE
    WHEN COALESCE(tcd.n_hijos, 0) = 0 THEN '99'
    ELSE TO_CHAR(tcd.n_hijos, 'FM00')
  END                                                       AS "TOTAL_HIJOS",
  CASE
    WHEN COALESCE(tcd.n_hijos_menores_edad, 0) = 0 THEN '99'
    ELSE TO_CHAR(tcd.n_hijos_menores_edad, 'FM00')
  END                                                       AS "MENORES_EDAD",
  ''                                                        AS "CUSTODIA",
  ''                                                        AS "PATRIA_POTESTAD",
  ''                                                        AS "PENSION_ALIM",
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
  ''                                                        AS "AQUESEDEDICA_DIV1",
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
  ''                                                        AS "AQUESEDEDICA_DIV2",
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