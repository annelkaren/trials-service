-- DROP PROCEDURE trials.proc_generar_secuencia(int4);


/** Creación de la secuencia para controlar el numero de expedientes de un juzgado */
CREATE OR REPLACE PROCEDURE trials.proc_generar_secuencia(IN fn_juzgado integer)
 LANGUAGE plpgsql
AS $procedure$
	DECLARE _secuencia VARCHAR(50); 
	BEGIN

	_secuencia:= 'seq_secuencia_juzgado_'||fn_juzgado;

	EXECUTE FORMAT('CREATE SEQUENCE IF NOT EXISTS %I START WITH 1', _secuencia);

	EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error al generar la secuencia';
	END;
$procedure$
;

-- DROP PROCEDURE trials.proc_eliminar_secuencia(int4);

/** Eliminación de una secuencia de control de numero de expedientes de un juzgado */
CREATE OR REPLACE PROCEDURE trials.proc_eliminar_secuencia(IN fn_juzgado integer)
 LANGUAGE plpgsql
AS $procedure$
	DECLARE _secuencia VARCHAR(50); 
	BEGIN
		_secuencia:= 'seq_secuencia_juzgado_'||fn_juzgado;

		EXECUTE format('DROP SEQUENCE IF EXISTS %I', _secuencia);

		EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'No se pudo eliminar la secuencia';
	END;
$procedure$
;

-- DROP PROCEDURE trials.proc_reiniciar_secuencias_juzgados(int4);

/** Reinicialización de las secuencias de control de numero de expedientes de los juzgaods **/
CREATE OR REPLACE PROCEDURE trials.proc_reiniciar_secuencias_juzgados(IN fn_juzgado integer DEFAULT NULL::integer)
 LANGUAGE plpgsql
AS $procedure$
	DECLARE 
    cursor_juzgados CURSOR FOR select * from trials.tbl_juzgados j 
		where (case when fn_juzgado is null then 1 when j.pn_id = fn_juzgado then 1 else 0 end )=1;
	_secuencia varchar(50);

	BEGIN

		FOR tmp IN cursor_juzgados LOOP
			_secuencia := 'seq_secuencia_juzgado_' || tmp.pn_id;
			EXECUTE format('ALTER SEQUENCE IF EXISTS %I RESTART WITH 1', _secuencia );
			RAISE NOTICE 'Secuencia % reiniciada', _secuencia;
		END LOOP;

		EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error al reiniciar la secuencia %', _secuencia;
	END;
$procedure$
;


-- DROP FUNCTION trials.fun_generar_secuencia();

/** Function para el trigger de trg_generar_secuencia **/
CREATE OR REPLACE FUNCTION trials.fun_generar_secuencia()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
	BEGIN

		CALL proc_generar_secuencia(NEW.pn_id);

		RETURN NULL;

		EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error al generar la secuencia'; RETURN NULL;

	END;
$function$
;

-- DROP FUNCTION trials.fun_eliminar_secuencia();
/** Function para el trigger trg_eliminar_secuencia **/
CREATE OR REPLACE FUNCTION trials.fun_eliminar_secuencia()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
	BEGIN

		CALL trials.proc_eliminar_secuencia(OLD.pn_id);
		
		RETURN NULL;

		EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error al eliminar la secuencia';  RETURN NULL;
	END;
$function$
;

-- DROP FUNCTION trials.fun_generar_num_expediente(int4);

/** Funcion que devuelve un nuevo numero de expediente según el id de Juzgado */

CREATE OR REPLACE FUNCTION trials.fun_generar_num_expediente(fn_juzgado integer)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$

	DECLARE
		n_secuencia int;
		s_num_expediente VARCHAR(50);
		_secuencia VARCHAR(50);
		
	BEGIN
		_secuencia := 'seq_secuencia_juzgado_' || fn_juzgado;

		EXECUTE FORMAT('select nextval(''%I'')', _secuencia) into n_secuencia;

		s_num_expediente := lpad(n_secuencia::text, 5, '0') || '/' || EXTRACT('Year' FROM CURRENT_DATE);

		RETURN s_num_expediente;

		EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error al generar el numero de expediente'; RETURN NULL;

	END;
$function$
;