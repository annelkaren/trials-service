ALTER TABLE trials.TBL_BLOQUES ADD COLUMN J_DATA JSONB;

-- seteamos el intervalo de cirtas para cada bloque:
UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "08:30:00"}]}'
where PN_ID = 1;

UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "11:00:00"}]}'
where PN_ID = 2;

UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "08:30:00"}]}'
where PN_ID = 3;