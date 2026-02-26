package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;

public record NotificacionesSalasRecord(
    Integer idNotificacionSala,
    String numeroExpediente,
    String tipoSala,
    String nombreDestinatario,
    String correoElectronico,
    LocalDateTime fechaTermino,
    String rutaArchivo,
    String nombreArchivo,
    LocalDateTime fechaEnvio,
    LocalDateTime fechaLectura,
    LocalDateTime fechaEntrega
) {}
