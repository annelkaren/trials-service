-- Modifica la columna estado para guardar cuando se retorna a oficialia. --
ALTER TABLE TRIALS.TBL_MOVIMIENTOS 
ALTER COLUMN S_ESTADO TYPE VARCHAR(25);
