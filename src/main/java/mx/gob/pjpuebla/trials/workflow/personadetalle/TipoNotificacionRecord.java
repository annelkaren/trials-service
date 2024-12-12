package mx.gob.pjpuebla.trials.workflow.personadetalle;

import java.io.Serializable;

public record TipoNotificacionRecord (
    Integer ordinal,
    String name,
    String descripcion
) implements Serializable {}

