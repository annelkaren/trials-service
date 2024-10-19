DROP SEQUENCE IF EXISTS trials.SEQ_DOCUMENTO_FOLIO_ID;
CREATE SEQUENCE trials.SEQ_DOCUMENTO_FOLIO_ID;

DROP TABLE IF EXISTS trials.TBL_DOCUMENTO_FOLIO;

CREATE TABLE trials.TBL_DOCUMENTO_FOLIO (
	pn_id int NOT NULL,
	n_tipo_documento int NOT NULL,
	n_centro_trabajo_id int NOT NULL,
	n_tipo_centro_trabajo int NOT NULL,
	n_folio int DEFAULT 1 NOT NULL,
	n_year int DEFAULT EXTRACT('YEAR' FROM CURRENT_DATE) NOT NULL,
	PRIMARY KEY(pn_id),
	CONSTRAINT tbl_documento_folio_unique UNIQUE (n_tipo_documento,n_centro_trabajo_id,n_tipo_centro_trabajo, n_year)
);
