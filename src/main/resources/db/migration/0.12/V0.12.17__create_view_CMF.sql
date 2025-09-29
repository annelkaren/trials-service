CREATE OR REPLACE VIEW
  trials.reporte_cfm AS
SELECT
  'PENDIENTE' AS "ASUNTO",
  'PENDIENTE' AS "TIPO_RESOLUCIÓN",
  'PENDIENTE' AS "RECURSO_APELACIÓN",
  'PENDIENTE' AS "RECURSO_QUEJA",
  'PENDIENTE' AS "REVOCACIÓN",
  'PENDIENTE' AS "TIIPO_PERSONA_INVOLUCRADA",
  'PENDIENTE' AS "ETAPA_PROCESAL_SENTENCIA",
  'PENDIENTE' AS "ETAPA_PROCESAL_INACTIVIDAD",
  'PENDIENTE' AS "EXPEDIENTES_CON_SENTENCIA",
  'PENDIENTE' AS "EXPEDIENTES_PENDIENTES_CONCLUIR",
  'PENDIENTE' AS "EXPEDIENTES_PENDIENTES_CONCLUIR_ETAPA_PROCESAL",
  'PENDIENTE' AS "MEDIOS_PRUEBA_REGISTRADOS",
  'PENDIENTE' AS "TIPO_AMPARO",
  'PENDIENTE' AS "SISTEMA_ESCRITO",
  'PENDIENTE' AS "NOTIFICACIONES",
  'PENDIENTE' AS "EXPEDIENTE_SENTENCIA_ATENDIDOS",
  'PENDIENTE' AS "EXPEDIENTES_CONCLUIDOS",
  'PENDIENTE' AS "EXPEDIENTES_DADOS_BAJA_INACTIVIDAD",
  'PENDIENTE' AS "SEXO_PERSONAS_INVOLUCRADAS",
  'PENDIENTE' AS "TIPO_AUDIENCIAS_CELEBRADAS",
  'PENDIENTE' AS "TIPO_JUICIO",
  'PENDIENTE' AS "TOCAS_CONCLUIDOS",
  TO_CHAR(ca.t_fecha_alta, 'DD')                            AS "DIA_DEM",
  TO_CHAR(ca.t_fecha_alta, 'MM')                            AS "MES_DEM",
  TO_CHAR(ca.t_fecha_alta, 'YYYY')                          AS "AÑO_DEM"
FROM
  trials.tbl_documentos doc
  JOIN trials.tbl_carpetas ca ON ca.pn_id = doc.fn_carpeta
  JOIN trials.tbl_tipo_juicio tj ON tj.pn_id = ca.fn_tipo_juicio
  JOIN trials.tbl_juzgados j ON ca.fn_juzgado = j.pn_id
  JOIN trials.tbl_materias m ON j.fn_materia = m.pn_id
where
  m.pn_id IN (200, 250, 300)
  AND NOT EXISTS (
    SELECT
      1
    FROM
      jsonb_array_elements(
        CASE
          WHEN jsonb_typeof(doc.j_data -> 'tiposJuicios') = 'array' THEN doc.j_data -> 'tiposJuicios'
          WHEN doc.j_data ? 'tiposJuicios' THEN jsonb_build_array(doc.j_data -> 'tiposJuicios')
          ELSE '[]'::jsonb
        END
      ) AS ju
    WHERE
      (ju ->> 'id') ~ '^\d+$'
      AND (ju ->> 'id')::int IN (112, 113)
  );