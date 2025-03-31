-- Modifica la columna estado para guardar cuando se retorna a oficialia. --
ALTER TABLE TRIALS.TBL_MOVIMIENTOS 
MODIFY COLUMN S_ESTADO VARCHAR(25);