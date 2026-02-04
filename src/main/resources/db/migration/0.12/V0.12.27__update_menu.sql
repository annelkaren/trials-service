UPDATE trials.tbl_menus
set s_rol = s_rol || 'OFICIAL_MAYOR_JUZGADO'
WHERE s_nombre = 'Promociones sin expediente';
