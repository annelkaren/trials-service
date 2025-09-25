package mx.gob.pjpuebla.migracion.readers.amparos;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record AmparoMigracionRecordSave(
    Carpeta carpeta,
    LocalDate fechaPresentacion, //fecha
    Integer impugnacion, // Revisión
    String quejoso, // quejoso_1
    Integer tribunalId, // juzdist
    Integer salaId,  // juzdist
    String sentido, // concede
    String sentidoImpugnacion, // impugna
    String tipo, // I - INDIRECTO - D DIRECTO
    LocalDate fechaTermino, // fec_con
    String folio // clave
) {}
