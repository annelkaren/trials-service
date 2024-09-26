CREATE TABLE trials.tbl_audiencias (
	n_id int NOT NULL PRIMARY KEY,
	t_fecha_audiencia timestamp NOT NULL,
	n_tipo_audiencia int NOT NULL,
	n_asistencia_actor int NULL,
	n_asistencia_demandado int NULL,
	fn_sala int NOT NULL,
	fn_carpeta int NOT NULL,
	fn_bloque int NOT NULL,
	t_fecha_alta timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	t_fecha_edita timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	t_usuario_alta varchar DEFAULT CURRENT_USER NOT NULL,
	t_usuario_edita varchar DEFAULT CURRENT_USER NOT NULL,
	n_status int NULL
);

ALTER TABLE trials.tbl_audiencias ADD CONSTRAINT fk_salas FOREIGN KEY (fn_sala) REFERENCES trials.tbl_salas(pn_id);
ALTER TABLE trials.tbl_audiencias ADD CONSTRAINT fk_bloque FOREIGN KEY (fn_bloque) REFERENCES trials.tbl_bloques(pn_id);
CREATE SEQUENCE  seq_audiencias_id START WITH 1;