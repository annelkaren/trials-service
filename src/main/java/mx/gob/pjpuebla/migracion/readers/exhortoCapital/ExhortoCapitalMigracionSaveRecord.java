package mx.gob.pjpuebla.migracion.readers.exhortoCapital;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record ExhortoCapitalMigracionSaveRecord(
    String tramite,
    String destino,
    String observaciones,
    LocalDate fechaEntrega,
    LocalDate fechaDevolucion,
    Carpeta carpeta,
    String folio
) {}
