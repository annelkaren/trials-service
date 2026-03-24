package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record SentenciaMigracionSaveRecord(
    Carpeta carpeta,
    LocalDate fechaResolucion,
    TipoSentencia tipoSentencia,
    TipoResolucion tipoResolucion,
    String folio,
    String ruta,
    LocalDate fechaPublicacion

) {}
