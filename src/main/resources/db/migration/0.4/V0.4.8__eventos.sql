DROP SEQUENCE IF EXISTS trials.SEQ_EVENTOS_ID;
CREATE SEQUENCE trials.SEQ_EVENTOS_ID START WITH 1 INCREMENT BY 1;

DROP TABLE IF EXISTS trials.TBL_EVENTOS;

CREATE TABLE trials.tbl_eventos (
	pn_id int NOT NULL,
	n_version int NOT NULL,
	s_descripcion varchar NULL,
	t_dia_inicio date NOT NULL,
	t_dia_fin date NOT NULL,
	fn_juzgado int NULL,
	fn_oficialia int NULL,
	n_estado int not null,
	t_fecha_alta timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	t_fecha_edita timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	t_usuario_alta varchar DEFAULT CURRENT_USER NOT NULL,
	t_usuario_edita varchar DEFAULT CURRENT_USER NOT NULL
    PRIMARY KEY(pn_id);
);

ALTER TABLE trials.TBL_EVENTOS ADD CONSTRAINT fk_juzgado FOREIGN KEY(fn_juzgado) REFERENCES trials.TBL_JUZGADO(pn_id) ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE trials.TBL_EVENTOS ADD CONSTRAINT fk_oficialia FOREIGN KEY(fn_oficialia) REFERENCES trials.TBL_OFICIALIA(pn_id) ON UPDATE CASCADE ON DELETE RESTRICT;
