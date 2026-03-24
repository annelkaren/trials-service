-- INDICES EN TABLAS PARA AGILIZAR CONSULTAS: 

-- ADD COLUMNS:

ALTER TABLE acuerdos.entradas ADD estado_migracion INTEGER UNSIGNED DEFAULT 4 NULL;
ALTER TABLE acuerdos.acuerdos ADD migrado INTEGER UNSIGNED DEFAULT 0 NULL;
ALTER TABLE acuerdos.detalles_prom ADD migrado INTEGER UNSIGNED DEFAULT 0 NULL;
ALTER TABLE acuerdos.oficios ADD migrado INTEGER UNSIGNED DEFAULT 0 NULL;
ALTER TABLE acuerdos.exhortos_capital ADD COLUMN migrado INTEGER UNSIGNED DEFAULT 0;
ALTER TABLE acuerdos.exhorto_foraneo ADD COLUMN migrado INTEGER UNSIGNED DEFAULT 0;
ALTER TABLE acuerdos.amparo ADD COLUMN migrado INTEGER UNSIGNED DEFAULT 0;

-- INDICES:

 CREATE INDEX IDX_ENTRADAS_EXP_AMO_JUZ_STATUS ON acuerdos.entradas(expediente, amo, juzgado, status);
 CREATE INDEX IDX_DETALLES_PROM_CU_STATUS ON acuerdos.detalles_prom(cu, status);

            