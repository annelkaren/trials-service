UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "08:30:00"}]}'
where T_HORA_FINAL = '09:30:00';

UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "11:00:00"}]}'
where T_HORA_INICIAL = '11:00:00';

UPDATE TRIALS.TBL_BLOQUES 
SET J_DATA = '{"citas": [{"numCitas": 2, "horaCitas": "08:30:00"}]}'
where T_HORA_FINAL = '12:30:00';