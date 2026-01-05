package mx.gob.pjpuebla.migracion.readers.oficios;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public record OficiosMigracionSaveRecord(
    Carpeta carpeta,
    EstadoCarpeta estadoCarpeta,
    Integer oficio,
    String rutaOfi,
    Integer folio,
    String dependencia,
    String asunto,
    LocalDate fechaEmision,
    LocalDate fechaEntrega,
    String ruta,
    String motivo,
    EstadoAcuse estadoAcuse,
    String nombre
) {

}