-- Insertamos sala y la asociamos al juez de penal, y al bloque de 24 horas de penal.
INSERT INTO TRIALS.TBL_SALAS(PN_ID, N_VERSION, S_NOMBRE, N_ESTADO, FN_JUEZ_ID, FN_BLOQUE_ID, FN_JUZGADO_ID)
SELECT nextval('TRIALS.SEQ_SALA_ID'), 0, 2, 0, p.pn_id, 4, 8
FROM TBL_PERSONAS p WHERE S_NOMBRES = 'Pedro' AND S_APELLIDO_PATERNO = 'Mendez';  