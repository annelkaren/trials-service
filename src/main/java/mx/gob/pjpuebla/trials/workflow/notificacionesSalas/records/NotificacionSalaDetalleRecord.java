package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;
import java.util.List;

public record NotificacionSalaDetalleRecord(
        Integer idNotificacionSala,
        String numeroExpediente,
        String nombreSala,
        String tipoSala,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaTermino,
        String contenidoCorreo,
        String rutaArchivo,
        String nombreArchivo,
        Integer totalDestinatarios,
        Integer destinatariosExitosos,
        Integer destinatariosFallidos,
        List<NotificacionSalaDestinatarioRecord> destinatarios) {
}
