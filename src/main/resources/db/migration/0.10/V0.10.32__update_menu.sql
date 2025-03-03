UPDATE TRIALS.TBL_MENUS
SET S_ROL = 'ADMINISTRADOR_SISTEMA'
WHERE S_NOMBRE IN ('Sedes', 'Instituciones','Oficialías', 'Órgano jurisdiccional', 'Salas');