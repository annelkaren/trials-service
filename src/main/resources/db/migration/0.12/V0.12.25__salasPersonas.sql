-- 1) schema (opcional)
CREATE SCHEMA IF NOT EXISTS trials;

-- 2) sequence
CREATE SEQUENCE IF NOT EXISTS trials.sala_personas_seq
    INCREMENT BY 1
    MINVALUE 1
    START WITH 1;

-- 3) table
CREATE TABLE IF NOT EXISTS trials.tbl_salas_personas (
    PN_ID           INTEGER,
    N_VERSION      INTEGER,
    FN_PERSONA      INTEGER NOT NULL,
    FN_SALA         INTEGER NOT NULL,
    S_ROL             VARCHAR(50),
    ESTADO VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    T_FECHA_ALTA    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    T_FECHA_EDITA   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    S_USUARIO_ALTA  VARCHAR   NOT NULL DEFAULT CURRENT_USER,
    S_USUARIO_EDITA VARCHAR   NOT NULL DEFAULT CURRENT_USER
);

-- 4) default de PN_ID con sequence (idempotente)
ALTER TABLE trials.tbl_salas_personas
    ALTER COLUMN PN_ID SET DEFAULT nextval('trials.sala_personas_seq');

-- 5) constraints idempotentes
DO $$
BEGIN
    -- PK
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'pk_tbl_salas_personas'
    ) THEN
        ALTER TABLE trials.tbl_salas_personas
            ADD CONSTRAINT pk_tbl_salas_personas PRIMARY KEY (PN_ID);
    END IF;

    -- FK sala
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_sala_tbl_salas_personas'
    ) THEN
        ALTER TABLE trials.tbl_salas_personas
            ADD CONSTRAINT fk_sala_tbl_salas_personas
            FOREIGN KEY (FN_SALA)
            REFERENCES trials.tbl_salas (PN_ID);
    END IF;

    -- FK persona
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_persona_tbl_salas_personas'
    ) THEN
        ALTER TABLE trials.tbl_salas_personas
            ADD CONSTRAINT fk_persona_tbl_salas_personas
            FOREIGN KEY (FN_PERSONA)
            REFERENCES trials.tbl_personas (PN_ID);
    END IF;

    -- UNIQUE para evitar duplicados
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_persona_sala_tbl_salas_personas'
    ) THEN
        ALTER TABLE trials.tbl_salas_personas
            ADD CONSTRAINT uk_persona_sala_tbl_salas_personas
            UNIQUE (FN_PERSONA, FN_SALA);
    END IF;
END $$;
