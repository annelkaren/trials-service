-- Actualiza banseja de asignados
UPDATE TRIALS.TBL_MENUS
SET S_ROL = S_ROL || ',COMISARIO'
WHERE S_NOMBRE IN ('Libro de gobierno', 'Buscador', 'Oficios');
COMMIT;

