package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

public record AcuerdoNotificacionesRecord(
        Integer numAcuerdo,
        String nombreDestinatario,
        TipoNotificacion metodoNotificacion,
        EstadoNotificacion estatus,
        String comentarios
) {
}
