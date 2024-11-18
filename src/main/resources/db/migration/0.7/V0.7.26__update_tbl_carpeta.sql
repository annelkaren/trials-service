ALTER TABLE trials.tbl_carpetas ADD fn_carpeta_padre int NULL;
ALTER TABLE trials.tbl_carpetas ADD CONSTRAINT tbl_carpetas_tbl_carpetas_fk FOREIGN KEY (fn_carpeta_padre) REFERENCES trials.tbl_carpetas(pn_id);
ALTER TABLE trials.tbl_carpetas ALTER COLUMN s_folio TYPE varchar(25) USING s_folio::varchar(25);
ALTER TABLE trials.tbl_carpetas ALTER COLUMN s_expediente TYPE varchar(30) USING s_expediente::varchar(30);
