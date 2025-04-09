-- CREACIÓN DE MENÚ ETIQUETAS PARA OFICIAL MAYOR DE JUZGADO
INSERT INTO TRIALS.TBL_MENUS (pn_id, s_nombre, s_rol, s_link, n_order, fn_parent)
VALUES (40, 'Etiquetas', 'OFICIAL_MAYOR_JUZGADO', '/api/workflow/generacionEtiquetas', 11, 4);