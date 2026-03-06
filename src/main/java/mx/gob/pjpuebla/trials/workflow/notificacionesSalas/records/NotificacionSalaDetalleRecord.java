package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;
import java.util.List;

public record NotificacionSalaDetalleRecord(
        Integer idNotificacionSala,
        String numeroToca,
        Integer salaId,
        String nombreSala,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaTermino,
        String contenidoCorreo,
        String rutaArchivo,
        String nombreArchivo,
        List<NotificacionSalaDestinatarioRecord> destinatarios) {
}
