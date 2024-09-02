ALTER TABLE trials.tbl_domicilios DROP COLUMN s_estado;

ALTER TABLE trials.tbl_domicilios DROP COLUMN n_version;

ALTER TABLE trials.tbl_domicilios ADD COLUMN s_referencia varchar(250) default null;

ALTER TABLE trials.tbl_domicilios ADD COLUMN s_localidad varchar(250) default null;