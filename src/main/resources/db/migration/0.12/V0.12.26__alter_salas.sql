-- elimina restricción NOT NULL de la columna JUEZ de la
-- tabla para poder liberar un juez e una sala y que la sala quede libre.
ALTER TABLE trials.tbl_salas
ALTER COLUMN fn_juez_id DROP NOT NULL;
