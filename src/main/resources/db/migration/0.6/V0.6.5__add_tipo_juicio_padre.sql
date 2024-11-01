---- Agregar tipos Juicios
ALTER TABLE TRIALS.TBL_TIPO_JUICIO
ADD COLUMN FN_TIPO_JUICIO_PADRE_ORAL INT,
ADD COLUMN FN_TIPO_JUICIO_PADRE_TRAD INT;

ALTER TABLE TRIALS.TBL_TIPO_JUICIO
ADD CONSTRAINT FK_TIPO_JUICIO_PADRE_ORAL
FOREIGN KEY (FN_TIPO_JUICIO_PADRE_ORAL) REFERENCES TRIALS.TBL_TIPO_JUICIO (PN_ID),
ADD CONSTRAINT FK_TIPO_JUICIO_PADRE_TRAD
FOREIGN KEY (FN_TIPO_JUICIO_PADRE_TRAD) REFERENCES TRIALS.TBL_TIPO_JUICIO (PN_ID);

-- Modificar tipos juicios padres a hijos

UPDATE trials.tbl_tipo_juicio
SET fn_tipo_sistema = NULL,
    fn_tipo_juicio_padre_oral = (SELECT pn_id FROM trials.tbl_tipo_juicio WHERE s_nombre = 'Familiar Oralidad')
WHERE s_nombre IN (
    'Familiar Oralidad (Alimentos)',
    'Familiar Oralidad (Divorcio Incausado Unilateral)',
    'Familiar Oralidad (Guardia y Custodia)',
    'Familiar Oralidad (Visita y Convivencia)',
    'Familiar Oralidad (Divorcio Incausado Bilateral)'
);


UPDATE trials.tbl_tipo_juicio
SET fn_tipo_sistema = NULL,
    fn_tipo_juicio_padre_oral = (SELECT pn_id FROM trials.tbl_tipo_juicio WHERE s_nombre = 'Oralidad Mercantil (Oral)')
WHERE s_nombre IN (
    'Oralidad Mercantil (Oral Ejecutivo)',
    'Oralidad Mercantil (Providencia precautoria)',
    'Oralidad Mercantil (Medios preparatorios)'
);

--Insertar tipo de juicio Padre faltantes
INSERT INTO trials.tbl_tipo_juicio
(pn_id, n_version, s_nombre, n_estado, fn_tipo_sistema, fn_materia, t_fecha_alta, t_fecha_edita, s_usuario_alta, s_usuario_edita, fn_tipo_juicio_padre_oral, fn_tipo_juicio_padre_trad)
VALUES (
    nextval('trials.seq_tipo_juicio_id'),
    0,
    'Laboral (Oral)',
    0,
    (SELECT pn_id FROM trials.tbl_tipo_sistema WHERE s_nombre = 'Oral'),
    (SELECT pn_id FROM trials.tbl_materias WHERE s_nombre = 'LABORAL'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    '6b13785f-d213-4585-a76b-437ffe57c9c7',
    '6b13785f-d213-4585-a76b-437ffe57c9c7',
    NULL,
    NULL
);

