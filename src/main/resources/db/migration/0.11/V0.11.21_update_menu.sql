update trials.tbl_menus tm 
set s_rol = S_ROL || ',JUEZ'
where s_rol like '%SECRETARIO%'