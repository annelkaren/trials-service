package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.Rol;

public record PersonaDataRecord(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String tipoPartesNombre,
        Rol rol
) {
}
