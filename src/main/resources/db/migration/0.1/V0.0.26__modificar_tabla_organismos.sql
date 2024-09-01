-- si los datos actuales no son convertibles hacer esto antes del paso 1
-- se perderan los datos actuales.
TRUNCATE TABLE TRIALS.TBL_ORGANISMOS

-- Paso 1: Cambiar el tipo de dato de CHAR(1) a INT, asegurándote de que los datos actuales sean convertibles
ALTER TABLE TRIALS.TBL_ORGANISMOS
    ALTER COLUMN S_ESTADO TYPE INT USING S_ESTADO::INT;

-- Paso 2: Renombrar la columna
ALTER TABLE TRIALS.TBL_ORGANISMOS
    RENAME COLUMN S_ESTADO TO N_ESTADO;

-- Insert Nuevos
INSERT INTO TRIALS.TBL_ORGANISMOS (PN_ID, N_VERSION, S_NOMBRE, T_FECHA_ALTA, T_FECHA_EDITA, N_ESTADO, S_USUARIO_ALTA, S_USUARIO_EDITA)
VALUES (nextval('TRIALS.SEQ_ORGANISMOS_ID'), 0, 'CONSEJO DE LA JUDICATURA DEL PODER JUDICIAL DEL ESTADO DE PUEBLA', current_timestamp, current_timestamp, 0, '6b13785f-d213-4585-a76b-437ffe57c9c7', '6b13785f-d213-4585-a76b-437ffe57c9c7');
