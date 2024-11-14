ALTER TABLE TRIALS.TBL_DOCUMENTOS DROP COLUMN S_MOTIVO_EDITA;

UPDATE trials.tbl_menus set s_nombre = 'Órgano jurisdiccional' WHERE s_nombre = 'Juzgados';