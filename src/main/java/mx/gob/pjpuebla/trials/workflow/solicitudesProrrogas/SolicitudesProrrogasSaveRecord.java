package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoProrroga;

public record SolicitudesProrrogasSaveRecord(
    Integer solicitudProrrogaId,
    LocalDate fechaAutorizacion,
    String observaciones,
    EstadoProrroga estadoProrroga
) {
    
}
