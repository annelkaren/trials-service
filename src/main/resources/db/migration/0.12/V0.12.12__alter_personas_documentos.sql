-- se incrementa tamaño en nombre ya que se detecto en la migración que hay personas morales con nombres muy largos.
ALTER TABLE TRIALS.TBL_PERSONAS_DOCUMENTOS
ALTER COLUMN s_nombres TYPE VARCHAR(100);