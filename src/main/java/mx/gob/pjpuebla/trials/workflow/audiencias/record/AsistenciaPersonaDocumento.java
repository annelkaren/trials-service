package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import mx.gob.pjpuebla.trials.util.enums.Asistencia;

public record AsistenciaPersonaDocumento(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String rol,
        String tipoParte,
        Asistencia asistencia,
        String documentoIdentificacion
) {
}
