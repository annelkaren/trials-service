INSERT INTO TRIALS.TBL_MENUS (PN_ID, S_NOMBRE, S_ROL, S_LINK, N_ORDER, FN_PARENT)
VALUES (47, 'Estadística', 'ESTADISTICA', '', 1, null);

INSERT INTO TRIALS.TBL_MENUS (PN_ID, S_NOMBRE, S_ROL, S_LINK, N_ORDER, FN_PARENT)
VALUES (48, 'Reportes', 'ESTADISTICA', '/api/reportes/index', 1, 47);

INSERT INTO TRIALS.TBL_PERSONAS (PN_ID, N_VERSION, S_NOMBRES, S_APELLIDO_PATERNO, S_CURP, S_RFC, N_SEXO, S_CORREO_ELECTRONICO, S_TELEFONO, S_CELULAR, T_FECHA_NACIMIENTO, FN_ESTADO_CIVIL, S_OCUPACION, FN_ESCOLARIDAD, N_ESTADO, S_USUARIO, FN_DOMICILIO, FN_JUZGADO)
SELECT nextval('TRIALS.SEQ_PERSONAS_ID'), 0, 'Alejandra', 'Estadistica', '', '', 1, 'estadistica@estadistica.dev', '', '', '1992-10-10', EC.PN_ID, '', E.PN_ID, 0, 'cc64c866-f340-4a59-9fb7-cd6e9af63ad4', 13, null
FROM TRIALS.TBL_ESTADO_CIVIL AS EC, TRIALS.TBL_ESCOLARIDADES AS E WHERE EC.S_NOMBRE = 'Soltero/a' AND E.S_NOMBRE = 'No especificado';

CREATE TABLE TRIALS.TBL_REPORTES (
    PN_ID           INTEGER NOT NULL,
    S_CLAVE         CHARACTER VARYING(100) NOT NULL,
    S_NOMBRE        CHARACTER VARYING(100) NOT NULL,
    S_DESCRIPCION   CHARACTER VARYING(100) NOT NULL,
  	N_ORDEN         INTEGER NOT NULL,
    EXTRA_DATA      JSONB NULL
);

ALTER TABLE
  TRIALS.TBL_REPORTES
ADD
  CONSTRAINT TBL_REPORTES_PKEY PRIMARY KEY (PN_ID);

INSERT INTO TRIALS.TBL_REPORTES (PN_ID, S_CLAVE, S_NOMBRE, S_DESCRIPCION, N_ORDEN, EXTRA_DATA)
VALUES
(1, 'PENAL', 'Penal', 'Impartición De Justicia En Materia Penal', 1, '{"materias":[100],"tipoJuicios":null,"juiciosExcluidos":null}'),
(2, 'ADOLESCENTES', 'Penal adolescentes', 'Justicia Para Adolescentes', 2, '{"materias":[400],"tipoJuicios":null,"juiciosExcluidos":null}'),
(3, 'CFM', 'Civil, Familiar, Mercantil', 'Impartición De Justicia En Materia Civil, Familiar Y Mercantil', 3, '{"materias":[200,250,300],"tipoJuicios":null,"juiciosExcluidos":[112,113]}'),
(4, 'LABORAL', 'Laboral', 'Registro Administrativo En Materia Laboral (RALAB) INEGI', 4, '{"materias":[150],"tipoJuicios":null,"juiciosExcluidos":null}'),
(5, 'BANAVIM', 'BANAVIM', 'Órdenes De Protección BANAVIM', 5, '{}'),
(6, 'DIVORCIOS', 'Divorcios', 'Registro De Divorcios Incausados', 6, '{"materias":null,"tipoJuicios":[112,113],"juiciosExcluidos":null}');

ALTER TABLE TRIALS.TBL_DOMICILIOS ADD COLUMN N_MUNICIPIO INTEGER NULL;