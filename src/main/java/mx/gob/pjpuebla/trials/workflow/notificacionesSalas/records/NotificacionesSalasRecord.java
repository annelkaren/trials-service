package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;

public record NotificacionesSalasRecord(
    Integer idNotificacionSala,
    String numeroExpediente,
    Integer salaId,
    String nombreSala,
    LocalDateTime fechaEnvio,
    LocalDateTime fechaTermino,
    String nombreArchivo,
    Long totalDestinatarios,
    String resumenDestinatarios
) {}
