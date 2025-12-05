package mx.gob.pjpuebla.migracion.readers.actores;

import mx.gob.pjpuebla.migracion.readers.domicilio.DomicilioMigracion;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

public record ActoresMigracionSaveRecord(
    String tipoParte,
    TipoNotificacion tipoNotificacion,
    String correoElectronico,
    DomicilioMigracion domicilio,
    String nombre,
    String tipoPersona
) {}
