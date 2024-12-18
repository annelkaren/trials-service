package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import mx.gob.pjpuebla.trials.util.enums.Asistencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasResponseRecord;

public record AsistenciaAudienciaResponse(
        Integer id,
        Integer personaDocumentoId,
        AudienciasResponseRecord audienciasResponseRecord,
        Asistencia asistencia,
        String documentoIdentificacion,
        String urlDocumento
) {
}
