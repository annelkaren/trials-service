package mx.gob.pjpuebla.migracion.readers.amparos;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record AmparoMigracionRecordSave(
    Carpeta carpeta,
    LocalDate fechaPresenteacion,
    Integer amparoImpugnacion,
    String quejoso,
    Integer tribunalId,
    Integer salaId,
    String sentido,
    String sentidoImpugnacion,
    String tipo,
    LocalDate fechaTermino,
    String folio
) {}
