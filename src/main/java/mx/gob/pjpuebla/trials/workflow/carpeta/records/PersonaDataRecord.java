package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.io.Serializable;

public record PersonaDataRecord(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String tipoPartesNombre,
        Rol rol
) implements Serializable {
}
