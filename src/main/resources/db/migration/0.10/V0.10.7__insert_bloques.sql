--  creacion de bloque de 24 horas para audiencias de tipo penal. -- 
INSERT INTO TRIALS.TBL_BLOQUES (PN_ID, T_HORA_INICIAL, T_HORA_FINAL, N_ESTADO, T_FECHA_ALTA, J_DATA) 
VALUES (
    nextval('TRIALS.SEQ_BLOQUE_ID'), 
    '00:00:00', 
    '23:59:59',
    0, 
    CURRENT_TIMESTAMP,
    '{
      "citas": [
        { "numCitas": 1, "horaCitas": "00:00:00" },
        { "numCitas": 2, "horaCitas": "01:00:00" },
        { "numCitas": 3, "horaCitas": "02:00:00" },
        { "numCitas": 4, "horaCitas": "03:00:00" },
        { "numCitas": 5, "horaCitas": "04:00:00" },
        { "numCitas": 6, "horaCitas": "05:00:00" },
        { "numCitas": 7, "horaCitas": "06:00:00" },
        { "numCitas": 8, "horaCitas": "07:00:00" },
        { "numCitas": 9, "horaCitas": "08:00:00" },
        { "numCitas": 10, "horaCitas": "09:00:00" },
        { "numCitas": 11, "horaCitas": "10:00:00" },
        { "numCitas": 12, "horaCitas": "11:00:00" },
        { "numCitas": 13, "horaCitas": "12:00:00" },
        { "numCitas": 14, "horaCitas": "13:00:00" },
        { "numCitas": 15, "horaCitas": "14:00:00" },
        { "numCitas": 16, "horaCitas": "15:00:00" },
        { "numCitas": 17, "horaCitas": "16:00:00" },
        { "numCitas": 18, "horaCitas": "17:00:00" },
        { "numCitas": 19, "horaCitas": "18:00:00" },
        { "numCitas": 20, "horaCitas": "19:00:00" },
        { "numCitas": 21, "horaCitas": "20:00:00" },
        { "numCitas": 22, "horaCitas": "21:00:00" },
        { "numCitas": 23, "horaCitas": "22:00:00" },
        { "numCitas": 24, "horaCitas": "23:00:00" }
      ]
    }'
);
