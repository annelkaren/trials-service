DROP SEQUENCE IF EXISTS TRIALS.SEQ_DESAHOGO_AUD_ID;
CREATE SEQUENCE IF NOT EXISTS TRIALS.SEQ_DESAHOGO_AUD_ID START WITH 15 INCREMENT BY 1;
DROP TABLE IF EXISTS TRIALS.TBL_DESAHOGO_AUDIENCIA;

CREATE TABLE TRIALS.TBL_DESAHOGO_AUDIENCIA (
    PN_ID INT NOT NULL PRIMARY KEY DEFAULT NEXTVAL('TRIALS.SEQ_DESAHOGO_AUD_ID'),
    S_KEY VARCHAR(10) NOT NULL,
    S_NOMBRE VARCHAR(80) NOT NULL
);

INSERT INTO TRIALS.TBL_DESAHOGO_AUDIENCIA (PN_ID, S_KEY, S_NOMBRE)
VALUES
  (1, 'CON_POR_CO', 'Conclusión por Convenio'),
  (2, 'DES_DE_ACC', 'Desistimiento de la Acción'),
  (3, 'DES_DE_INS', 'Desistimiento de la Instancia'),
  (4, 'NUL_DE_ACT', 'Nulidad de la actuación'),
  (5, 'RESCUSA', 'Recusación'),
  (7, 'EMITI_FALL', 'Se emitió fallo'),
  (8, 'DESECHAR', 'Desechar'),
  (9, 'INTERRUP', 'Interrupción'),
  (10, 'CAD_DE_INS', 'Caducidad de la Instancia'),
  (11, 'INCO_FUNDA', 'Incompetencia Fundada'),
  (12, 'SOBRESEIMI', 'Sobreseimiento'),
  (13, 'CONEXIDAD', 'Conexidad'),
  (14, 'OTRO', 'Otro');
