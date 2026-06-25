INSERT INTO trials.tbl_menus (
    pn_id,
    s_nombre,
    s_rol,
    s_link,
    n_order,
    fn_parent
)
VALUES
    (58, 'Visitaduria', 'VISITADOR', '', 1, null),
    (59, 'Turnos', 'VISITADOR', 'api/visitaduria/turnos',2,58),
    (60, 'Acuerdos', 'VISITADOR', 'api/visitaduria/acuerdos',3,58);