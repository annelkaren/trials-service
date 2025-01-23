-- insertamos los mismos tipos de juicio que estan en penal y los vinculamos a la materia de justicia para adolescentes a excepción del juicio patre el cual es excluido.

INSERT INTO TRIALS.tbl_tipo_juicio(pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia, n_tipo_causa)
select nextval('TRIALS.SEQ_TIPO_JUICIO_ID'), 0, TJ.S_NOMBRE, TJ.N_ESTADO, TJ.FN_TIPO_SISTEMA, 400, TJ.N_TIPO_CAUSA
FROM TRIALS.tbl_tipo_juicio TJ 
JOIN TRIALS.TBL_MATERIAS MATERIA ON TJ.FN_MATERIA = MATERIA.PN_ID AND MATERIA.S_NOMBRE = 'PENAL' and TJ.s_nombre != 'Penal';