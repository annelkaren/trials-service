
CREATE OR REPLACE VIEW
  trials.reporte_divorcios AS
SELECT
  -- /* ===== DATOS DEL REGISTRO ===== */
  21::SMALLINT 													AS "ENT_REG",
  '' 															AS "MPIO_REG",
  ca.s_expediente 											    AS "NuM_EXPDIV",
  6::SMALLINT													AS "CVE_EST",
  -- /* ===== DATOS DEL MATRIMONIO ===== */
  tcd.s_numero_acta_matrimonio 					                AS "NuM_ACTAMAT",
  tcd.s_entidad													AS "ENT_REGMAT",
  tcd.s_municipio												AS "MPIO_REGMAT",
  tcd.s_localidad 											    AS "LOC_REGMAT",
  TO_CHAR(tcd.t_fecha_registro, 'DD')		                    AS "DIA_MAT",
  TO_CHAR(tcd.t_fecha_registro, 'MM')		                    AS "MES_MAT",
  TO_CHAR(tcd.t_fecha_registro, 'YYYY')	                        AS "AÑO_MAT",
  --/* ===== DATOS DEL DIVORCIO ===== */
  TO_CHAR(ca.t_fecha_alta, 'DD')				                AS "DIA_DEM",
  TO_CHAR(ca.t_fecha_alta, 'MM')				                AS "MES_DEM",
  TO_CHAR(ca.t_fecha_alta, 'YYYY')			                    AS "AÑO_DEM",
  tcd.s_localidad 											    AS "DIA_SEN",
  tcd.s_localidad 											    AS "MES_SEN",
  tcd.s_localidad 											    AS "AÑO_SEN",
  tcd.s_localidad 											    AS "DIA_EJEC",
  tcd.s_localidad 											    AS "MES_EJEC",
  tcd.s_localidad 											    AS "AÑO_EJEC",
  CASE
  WHEN EXISTS (
    SELECT 1
    FROM jsonb_array_elements(doc.j_data->'tiposJuicios') ju
    WHERE (ju->>'id')::int = 112
  )
  THEN 1
  ELSE 39
	END::SMALLINT 										        AS "PERS_INICJUIC",
  tcd.s_localidad 											    AS "A FAVOR",
  28::SMALLINT 									  			    AS "CAUSA1",
  CASE
  WHEN COALESCE(tcd.n_hijos, null) = 0
  THEN '99'
  ELSE TO_CHAR(tcd.n_hijos, 'FM00')
	END::SMALLINT 								                AS "TOTAL_HIJOS",
  CASE
  WHEN COALESCE(tcd.n_hijos_menores_edad, null) = 0
  THEN '99'
  ELSE TO_CHAR(tcd.n_hijos_menores_edad, 'FM00')
	END 									  			        AS "MENORES_EDAD",
  28::smallint 									  			    AS "CUSTODIA",
  28::smallint 									  			    AS "PATRIA_POTESTAD",
  28::smallint 									  			    AS "PENSION_ALIM",
  -- /* ===== DATOS DEL DIVORCIANTE 1 ===== */
  28::smallint 									  			    AS "SEXO_DIV1",
  28::smallint 									  			    AS "NAC_DIV1",
  28::smallint 									  	            AS "EDAD_DIV1",
  28::smallint 									  	            AS "AÑONAC_DIV1",
  28::smallint 									  	            AS "EDOCONY_ANTMAT_DIV1",
  28::smallint 									  	            AS "ENT_RESHAB_DIV1",
  28::smallint 									  	            AS "MPIO_RESHAB_DIV1",
  28::smallint 									  	            AS "LOC_RESHAB_DIV1",
  28::smallint 									  	            AS "ESCOL_DIV1",
  28::smallint 									  	            AS "AQUESEDEDICA_DIV1",
  28::smallint 									  	            AS "POSICENELTRABAJO_DIV1",
  -- /* ===== DATOS DEL DIVORCIANTE 2 ===== */
  28::smallint 									  	            AS "SEXO_DIV2",
  28::smallint 									  	            AS "NAC_DIV2",
  28::smallint 									  	            AS "EDAD_DIV2",
  28::smallint 									  	            AS "AÑONAC_DIV2",
  28::smallint 									  	            AS "EDOCONY_ANTMAT_DIV2",
  28::smallint 									  	            AS "ENT_RESHAB_DIV2",
  28::smallint 									  	            AS "MPIO_RESHAB_DIV2",
  28::smallint 									  	            AS "LOC_RESHAB_DIV2",
  28::smallint 									  	            AS "ESCOL_DIV2",
  28::smallint 									  	            AS "AQUESEDEDICA_DIV2",
  28::smallint 									  	            AS "POSICENELTRABAJO_DIV2",
  28::smallint 									  	            AS "OBSERVACIONES"
FROM
  trials.tbl_documentos doc
  INNER JOIN trials.tbl_carpetas ca ON doc.fn_carpeta = ca.pn_id
  INNER JOIN trials.tbl_tipo_juicio tj ON ca.fn_tipo_juicio = tj.pn_id
  INNER JOIN trials.tbl_carpeta_detalle tcd ON ca.pn_id = tcd.fn_carpeta
WHERE
  (
    jsonb_typeof(doc.j_data -> 'tiposJuicios') = 'array'
    AND EXISTS (
      SELECT
        1
      FROM
        jsonb_array_elements(doc.j_data -> 'tiposJuicios') ju
      WHERE
        (ju ->> 'id')::int = ANY (ARRAY[112, 113])
    )
  )