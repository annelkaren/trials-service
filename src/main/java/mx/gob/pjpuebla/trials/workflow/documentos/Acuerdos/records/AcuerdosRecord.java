package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record AcuerdosRecord(
   Integer numeroAcuerdo,
   LocalDate fechaResolucion,
   String resumen,
   EstadoCarpeta estatus
) { }
