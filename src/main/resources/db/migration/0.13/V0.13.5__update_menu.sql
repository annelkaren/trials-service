UPDATE trials.tbl_menus
SET S_ROL = S_ROL || ',COMISARIO_DE_SALA' 
WHERE PN_ID = 4;

insert into  trials.tbl_menus(PN_ID, S_NOMBRE, S_ROL, S_LINK, N_ORDER, FN_PARENT)
VALUES (57, 'Notificaciones', 'COMISARIO_DE_SALA', '/api/bandeja/notificaciones/sala', 17, 4);