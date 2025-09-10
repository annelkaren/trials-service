-- indica si un documento ha sido migrado o no, se almacena como enum
ALTER TABLE TRIALS.TBL_DOCUMENTOS ADD COLUMN N_MIGRADO INTEGER;

-- al haber muchos registrso duplicados y para no ensuciar la ifnormación del sistema actual se opto por una columna de institucion historica
ALTER TABLE TRIALS.TBL_DOCUMENTOS 
ADD S_INSTITUCION_HISTORICA VARCHAR(255);

-- al necesitar el campo id historico y para no afectar secuencias del sistema actual se opto por crear esta columna. 
ALTER TABLE TRIALS.TBL_DOCUMENTOS 
ADD N_ID_HISTORICO INTEGER;
