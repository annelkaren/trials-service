
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(62, 'Central de Comisarios', 'ADMINISTRADOR_CENTRAL_COM,COM_CENTRAL,COMISARIOS', '', '6', NULL);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(63, 'Entrada', 'ADMINISTRADOR_CENTRAL_COM', '/api/central-comisarios/entrada', '1', 62);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(64, 'Oficios asignados', 'ADMINISTRADOR_CENTRAL_COM,COM_CENTRAL', '/api/central-comisarios/oficios-asignados', '2', 62);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(65, 'Oficios enviados', 'COMISARIO', '/api/central-comisarios/oficios-enviados', '3', 62);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(66, 'Histórico', 'ADMINISTRADOR_CENTRAL_COM', '/api/central-comisarios/historico', '4', 62);