package mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records;

import java.io.Serializable;

public record AcuerdoPromocionesRecord(
    Integer id,
    String nombre,
    String nombreArchivo,
    String recomendacion,
    Integer seleccionado
) implements Serializable {}
