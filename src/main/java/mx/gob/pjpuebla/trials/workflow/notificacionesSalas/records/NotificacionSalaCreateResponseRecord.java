package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;

public record NotificacionSalaCreateResponseRecord(
        Integer idNotificacionSala,
        String numeroExpediente,
        Integer salaId,
        String nombreSala,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaTermino,
        String nombreArchivo,
        Integer totalDestinatarios,
        Integer destinatariosExitosos,
        Integer destinatariosFallidos) {
}
