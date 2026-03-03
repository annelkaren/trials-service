package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;

public record NotificacionesSalasRecord(
    Integer idNotificacionSala,
    String numeroExpediente,
    String nombreSala,
    String tipoSala,
    LocalDateTime fechaEnvio,
    LocalDateTime fechaTermino,
    String nombreArchivo,
    Long totalDestinatarios,
    String resumenDestinatarios
) {}
