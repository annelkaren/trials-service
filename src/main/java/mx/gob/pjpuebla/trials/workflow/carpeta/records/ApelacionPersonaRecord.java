package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record ApelacionPersonaRecord(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        Integer tipoPartes
) implements Serializable {
}
