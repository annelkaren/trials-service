INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (36, 'Bandeja', 'LITIGANTE', '/api/bandeja/', 1, null);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (37, 'Notificaciones', 'LITIGANTE', '/api/bandeja/notificaciones', 1, 35);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (38, 'Expedientes', 'LITIGANTE', '/api/bandeja/expedientes', 2, 35);

INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (39, 'Promociones electrónicas', 'LITIGANTE', '/api/bandeja/promociones', 3, 35);