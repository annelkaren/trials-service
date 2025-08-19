-- se agrega columna en juzgados para relacionar a los juzgados historicos. 

ALTER TABLE trials.tbl_juzgados 
ADD column IF NOT EXISTS s_clave_juzgado varchar(10);

ALTER TABLE trials.tbl_juzgados 
ADD column IF NOT EXISTS s_tabla_ubi varchar(150);