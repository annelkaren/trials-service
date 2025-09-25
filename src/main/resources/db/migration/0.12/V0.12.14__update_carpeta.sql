-- actualiza columna con 0 para indicar que todos los expedientes que se encuentran ya dados de alta en base no han sido migrados, son nativos del sistema.
update trials.tbl_carpetas
set n_migrado = 0
where n_migrado is null;


-- aplicamos lo mismo para documentos:

update trials.tbl_documentos td 
set n_migrado = 0
where td.n_migrado is null;

-- alter en ambos para nuevos elementos creados:
ALTER TABLE trials.tbl_carpetas
ALTER COLUMN n_migrado
SET DEFAULT 0;

ALTER TABLE trials.tbl_documentos
ALTER COLUMN n_migrado
SET DEFAULT 0;