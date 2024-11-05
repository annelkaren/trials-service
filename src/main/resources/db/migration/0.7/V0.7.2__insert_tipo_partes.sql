ALTER TABLE trials.tbl_tipo_partes
ALTER COLUMN s_nombre TYPE VARCHAR(100);

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Civil (Juicio de extincion de dominio)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Mercantil (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Oralidad Mercantil (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante - Apoderado legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante - Apoderado legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Ministerio Publico', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Representante del Sindicato', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - IMSS', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Infonavit', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante - Apoderado legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal de la persona moral actora', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante - Apoderado legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal de la persona moral demandada', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Ministerio Publico', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Representante del Sindicato', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - IMSS', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Infonavit', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Laboral (Oral)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado autorizado, pero no patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado autorizado, pero no patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Ministerio Publico', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar Oralidad'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Abogado autorizado, pero no patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Actor - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Promovente', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado autorizado, pero no patrono', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Abogado litigante', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Demandado - Representante legal', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Ministerio Publico', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

INSERT INTO trials.tbl_tipo_partes (pn_id, n_version, s_nombre, fn_tipo_juicio, n_estado, s_usuario_alta, s_usuario_edita)
VALUES (nextval('trials.seq_tipo_partes_id'), 0, 'Tercero Interesado - Testigo', (SELECT PN_ID FROM trials.tbl_tipo_juicio WHERE S_NOMBRE = 'Familiar (Tradicional)'), 0,
'6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');

