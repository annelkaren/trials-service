-- Agregar  columna 
ALTER TABLE TRIALS.tbl_audiencias
ADD COLUMN s_descripcion VARCHAR(180) NULL;

-- Permitir valores NULL 
ALTER TABLE TRIALS.tbl_audiencias
ALTER COLUMN fn_bloque DROP NOT NULL;
