package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.time.LocalDate;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record AcuerdosMigracionSaveRecord(
    Carpeta carpeta,
    String tipoAcuerdo,
    LocalDate fechaResolucion,
    List<String> rubros,
    String folio,
    String ruta,
    LocalDate fechaPublicacion
) {}
