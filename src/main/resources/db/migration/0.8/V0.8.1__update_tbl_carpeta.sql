ALTER TABLE trials.tbl_carpetas ADD fn_tipo_pieza int NULL;
ALTER TABLE trials.tbl_carpetas ADD CONSTRAINT tbl_carpetas_tipo_pieza_fk FOREIGN KEY (fn_tipo_pieza) REFERENCES trials.tbl_tipo_pieza(pn_id);

