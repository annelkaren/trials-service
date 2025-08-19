-- actualiza la columna cu para todos los registros.

UPDATE trials.tbl_carpetas  tc
SET s_cu = tj.s_clave_juzgado || REPLACE(tc.s_expediente, '/', '')
FROM trials.tbl_juzgados tj
WHERE tj.pn_id = tc.fn_juzgado;

-- actualiza folio de todas las carpetas con apoyo de la tabla configuracion

UPDATE trials.tbl_carpetas tc
SET s_folio = s_folio || (
    SELECT s_valor 
    FROM trials.tbl_configuraciones 
    WHERE s_propiedad = 'SERIE'
);