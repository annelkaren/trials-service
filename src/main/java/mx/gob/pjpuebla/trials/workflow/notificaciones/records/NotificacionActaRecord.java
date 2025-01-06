package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

public record NotificacionActaRecord(
        Integer id,
        String estadoNotificacion,
        String nota,
        String razon,
        String urlDocumento
) {
}
