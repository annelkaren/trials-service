drop sequence IF EXISTS TRIALS.SEQ_CUESTIONARIOS_ID;
create sequence TRIALS.SEQ_CUESTIONARIOS_ID start with 100;
alter sequence TRIALS.SEQ_CUESTIONARIOS_ID increment by 1;

drop table IF EXISTS TRIALS.TBL_CUESTIONARIOS;
create TABLE TRIALS.TBL_CUESTIONARIOS(
	PN_ID INT NOT NULL,
	S_PREGUNTAS VARCHAR(250),
    N_LISTA INT,
    N_TIPO INT,
	PRIMARY KEY(PN_ID)
);


-- General Primera Instancia
--(Preguntas Para las Materias de primera instancia que no tengan su propia lista)
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se debe guardar sigilo respecto al archivo?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿La sentencia fue dictada por un órgano jurisdiccional auxiliar?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿La sentencia contiene información clasificada como confidencial (Art. 134 LTAIPEP)?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Secretario que certifica', 0, 1);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se elaboró bajo el formato de lectura fácil?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'La sentencia o resolución se emitió conforme a un tratado internacional en materia de Derechos Humanos', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se aplicaron criterios de perspectiva de género?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos humanos fundamentales analizados', 0, 2);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos humanos fundamentales analizados específicos', 0, 3);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Esta sentencia se emitió con aplicación efectiva de un ordenamiento internacional y/o nacional de protección a los derechos de las mujeres, la igualdad y no discriminación?', 0, 0);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Escriba brevemente el tema del asunto', 0, 1);

insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Hubo solicitud de reparación del daño o fue decretada en la sentencia', 0, 4);

insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Habla lengua originaria', 0, 5);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Pertenece al grupo LGTBIQ+', 0, 5);
insert into TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
values (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Padece alguna discapacidad', 0, 5);


-- Lista para Salas Penales
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿La sentencia fue dictada por un órgano jurisdiccional auxiliar?', 1, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se elaboró bajo el formato de lectura fácil?', 1, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'La sentencia o resolución se emitió conforme a un tratado internacional en materia de Derechos Humanos', 1, 0 );
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se aplicaron criterios de perspectiva de género?', 1, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos Humanos fundamentales analizados', 1, 2);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos Humanos fundamentales analizados específicos', 1, 3);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Esta sentencia se emitió con aplicación efectiva de un ordenamiento internacional y/o nacional de protección a los derechos de las mujeres, la igualdad y no discriminación?', 1, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Hubo solicitud de reparación del daño o fue decretada en la sentencia', 1, 4);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Habla lengua originaria', 1, 5);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Pertenece al grupo LGTBIQ+', 1, 5);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Padece alguna discapacidad', 1, 5);

--Lista para Salas
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se debe guardar sigilo respecto a este archivo?', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿La sentencia fue dictada por un órgano jurisdiccional auxiliar?', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿La sentencia contiene información clasificada como confidencial (Art. 134 LTAIPEP)?', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Secretario que certifica', 2, 1);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se elaboró bajo el formato de lectura fácil?', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'La sentencia o resolución se emitió conforme a un tratado internacional en materia de Derechos Humanos', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Se aplicaron criterios de perspectiva de género?', 2, 0);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos humanos fundamentales analizados', 2, 2);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Derechos humanos fundamentales analizados específicos', 2, 3 );
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), '¿Esta sentencia se emitió con aplicación efectiva de un ordenamiento internacional y/o nacional de protección a los derechos de las mujeres, la igualdad y no discriminación?', 2, 0 );
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Escriba brevemente el tema del asunto', 2, 1);
INSERT INTO TRIALS.TBL_CUESTIONARIOS(PN_ID, S_PREGUNTAS, N_LISTA, N_TIPO)
VALUES (nextval('TRIALS.SEQ_CUESTIONARIOS_ID'), 'Hubo solicitud de reparación del daño o fue decretada en la sentencia', 2, 4);
