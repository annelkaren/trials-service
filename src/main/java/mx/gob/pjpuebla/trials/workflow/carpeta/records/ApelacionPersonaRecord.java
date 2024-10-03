package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.Rol;

public record ApelacionPersonaRecord(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        Integer tipoPartes
) {
}
