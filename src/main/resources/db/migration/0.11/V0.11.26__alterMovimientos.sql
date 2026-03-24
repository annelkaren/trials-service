-- cambio en columna observaciones que es la concatenación de todos los anexos.
ALTER TABLE trials.tbl_movimientos ALTER COLUMN s_motivo TYPE varchar USING s_motivo::varchar;

-- cambio en columna de anexo
ALTER TABLE trials.tbl_anexos ALTER COLUMN s_nombre TYPE varchar USING s_nombre::varchar;
