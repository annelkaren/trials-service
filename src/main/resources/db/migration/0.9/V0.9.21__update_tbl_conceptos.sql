ALTER TABLE TRIALS.tbl_conceptos
ADD COLUMN FN_TIPO_JUICIO INTEGER;

-- Agregar restricción de llave foránea
ALTER TABLE TRIALS.tbl_conceptos
ADD CONSTRAINT FK_TIPO_JUICIO
FOREIGN KEY (FN_TIPO_JUICIO)
REFERENCES TRIALS.tbl_tipo_juicio (PN_ID);

-- Crear índice en la nueva columna
CREATE INDEX IDX_FN_TIPO_JUICIO1
ON TRIALS.tbl_conceptos (FN_TIPO_JUICIO);
