INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (42, 'Consulta pública', 'LITIGANTE', '', 2, null);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (43, 'Libro de gobierno', 'LITIGANTE', '/api/cp/libro', 1, 42);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (44, 'Kiosko', 'LITIGANTE', '/api/cp/kiosko', 2, 42);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (45, 'Versiones públicas', 'LITIGANTE', '/api/cp/sentencia', 3, 42);

INSERT INTO TRIALS.TBL_TIPO_PARTES(PN_ID, N_VERSION, S_NOMBRE, FN_TIPO_JUICIO, N_ESTADO)
VALUES(nextval('TRIALS.SEQ_TIPO_PARTES_ID'), 0, 'Actor', 116, 0);

INSERT INTO TRIALS.TBL_TIPO_PARTES(PN_ID, N_VERSION, S_NOMBRE, FN_TIPO_JUICIO, N_ESTADO)
VALUES(nextval('TRIALS.SEQ_TIPO_PARTES_ID'), 0, 'Demandado', 116, 0);