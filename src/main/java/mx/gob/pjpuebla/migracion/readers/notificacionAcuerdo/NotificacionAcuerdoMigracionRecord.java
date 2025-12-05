package mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

public record NotificacionAcuerdoMigracionRecord(
    Integer idNotificacionLegacy,
    Integer claveAcuerdo,
    TipoNotificacion tipoNotificacion,
    String notas

) {}
