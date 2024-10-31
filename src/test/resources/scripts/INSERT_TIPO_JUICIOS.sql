INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (100, 0, 'Laboral (Tradicional)', 0, 100, 150,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (101, 0, 'Mercantil (Tradicional)', 0, 100, 200,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (102, 0, 'Oralidad Mercantil (Oral)', 0, 101, 200,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (103, 0, 'Oralidad Mercantil (Oral Ejecutivo)', 0, 101, 200,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (104, 0, 'Oralidad Mercantil (Providencia precautoria)', 0, 101, 200,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (105, 0, 'Oralidad Mercantil (Medios preparatorios)', 0, 101, 200,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (106, 0, 'Civil (Tradicional)', 0, 100, 300,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (107, 0, 'Civil (Oral)', 0, 101, 300,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (108, 0, 'Civil (Juicio de extincion de dominio)', 0, 101, 300,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (109, 0, 'Familiar (Tradicional)', 0, 100, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (110, 0, 'Familiar Oralidad', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita, fn_tipo_juicio_padre_oral)
VALUES (111, 0, 'Familiar Oralidad (Alimentos)', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7', 110);
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita, fn_tipo_juicio_padre_oral)
VALUES (112, 0, 'Familiar Oralidad (Divorcio Incausado Unilateral)', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7',110);
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita, fn_tipo_juicio_padre_oral)
VALUES (113, 0, 'Familiar Oralidad  (Divoricio Incausado Bilateral)', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7',110);
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (114, 0, 'Familiar Oralidad (Guardia y Custodia)', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
INSERT INTO tbl_tipo_juicio (pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia,
                             s_usuario_alta, s_usuario_edita)
VALUES (115, 0, 'Familiar Oralidad (Visita y Convivencia)', 0, 101, 250,
        '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
