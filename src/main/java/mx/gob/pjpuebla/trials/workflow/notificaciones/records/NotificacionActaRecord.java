package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;

public record NotificacionActaRecord(
        Integer id,
        String estadoNotificacion,
        String nota,
        String razon,
            String urlDocumento
) {
}
