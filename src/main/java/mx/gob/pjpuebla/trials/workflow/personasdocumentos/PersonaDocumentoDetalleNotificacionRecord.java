package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;

public record PersonaDocumentoDetalleNotificacionRecord(
    String tipoNotificacion,
    DomicilioRecord domicilio,
    String correoNotificacion
) {}
