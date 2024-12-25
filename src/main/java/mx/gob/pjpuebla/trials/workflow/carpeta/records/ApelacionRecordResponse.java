package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.io.Serializable;

public record ApelacionRecordResponse(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String pseudonimo,
        String tipoPersona,
        Rol rol,
        Integer carpeta,
        String tipoPartesNombre,
        Integer tiposPartesId
) implements Serializable {
}
