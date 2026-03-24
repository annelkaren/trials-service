package mx.gob.pjpuebla.migracion.readers.exhortoForaneo;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record ExhortoForaneoMigracionSaveRecord(
    String tramite,
    String observaciones,
    Carpeta carpeta,
    String folio
) {}
