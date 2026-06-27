
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(58, 'Central de Comisarios', 'ADMINISTRADOR_CENTRAL_COM,COM_CENTRAL,COMISARIOS', '', '6', NULL);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(59, 'Entrada', 'ADMINISTRADOR_CENTRAL_COM', '/api/central-comisarios/entrada', '1', 58);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(60, 'Oficios asignados', 'ADMINISTRADOR_CENTRAL_COM,COM_CENTRAL', '/api/central-comisarios/oficios-asignados', '2', 58);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(61, 'Oficios enviados', 'COMISARIO', '/api/central-comisarios/oficios-asignados', '3', 58);
INSERT INTO trials.tbl_menus
(pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES(62, 'Histórico', 'ADMINISTRADOR_CENTRAL_COM', '/api/central-comisarios/historico', '4', 58);