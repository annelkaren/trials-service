INSERT INTO trials.tbl_materias
(pn_id, n_version, s_nombre, n_estado, t_fecha_alta, t_fecha_edita, s_usuario_alta, s_usuario_edita)
VALUES(nextval('trials.seq_materias_id'), 0, 'JUSTICIA PARA ADOLESCENTES', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');