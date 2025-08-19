-- actualiza la columna cu para todos los registros.

UPDATE trials.tbl_carpetas  tc
SET cu = tj.s_clave_juzgado || REPLACE(tc.s_expediente, '/', '')
FROM trials.tbl_juzgados tj
WHERE tj.pn_id = tc.fn_juzgado;