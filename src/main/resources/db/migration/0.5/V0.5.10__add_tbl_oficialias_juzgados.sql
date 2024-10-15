CREATE TABLE trials.tbl_oficialias_juzgados (
    fn_oficialia int4 NOT NULL,
    fn_juzgado int4 NOT NULL,
    CONSTRAINT uc_oficialias_juzgados UNIQUE (fn_oficialia, fn_juzgado)
);

CREATE INDEX idx_oficialiasjuzgados_juzgado ON trials.tbl_oficialias_juzgados USING btree (fn_juzgado);
CREATE INDEX idx_oficialiasjuzgados_oficialia ON trials.tbl_oficialias_juzgados USING btree (fn_oficialia);

ALTER TABLE trials.tbl_oficialias_juzgados ADD CONSTRAINT tbl_oficialias_juzgados_fn_juzgado_fkey
    FOREIGN KEY (fn_juzgado) REFERENCES trials.tbl_juzgados(pn_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE trials.tbl_oficialias_juzgados ADD CONSTRAINT tbl_oficialias_juzgados_fn_oficialia_fkey
    FOREIGN KEY (fn_oficialia) REFERENCES trials.tbl_oficialias(pn_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;
