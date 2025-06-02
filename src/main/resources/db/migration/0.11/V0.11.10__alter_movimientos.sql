-- Agrega la columna motivo_prorroga (VARCHAR de hasta 300 caracteres)
ALTER TABLE trials.tbl_movimientos
ADD COLUMN s_motivo_prorroga VARCHAR(300);


-- Agrega estado de prorroga en tabla de movimientos.
ALTER TABLE trials.tbl_movimientos
ADD COLUMN n_estado_prorroga Integer;

-- Agrega FECHA de prorroga en tabla de movimientos.
ALTER TABLE trials.tbl_movimientos
ADD COLUMN t_fecha_prorroga DATE;