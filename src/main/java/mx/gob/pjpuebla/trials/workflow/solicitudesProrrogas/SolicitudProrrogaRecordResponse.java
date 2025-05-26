package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.time.LocalDate;

public record SolicitudProrrogaRecordResponse(
    Integer solicitudProrrogaId,
    String estadoProrroga,
    LocalDate fechaSolicitada
) {
    
}
