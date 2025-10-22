INSERT INTO trials.tbl_menus (
    pn_id,
    s_nombre,
    s_rol,
    s_link,
    n_order,
    fn_parent
)
VALUES
    (51, 'Promociones sin expediente', 'CAPTURISTA', '/api/workflow/promocion/sinExpedientes', 9, 3),
    (52, 'Promociones sin expediente', 'CAPTURISTA', '/api/workflow/bandeja/promociones/sinExpedientes', 16, 4);
