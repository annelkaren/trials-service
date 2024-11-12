package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import java.time.LocalDate;

public record AcuerdosRecord(
   Integer numeroAcuerdo,
   LocalDate fechaPublicacion,
   String resumen,
   String estatus
) { }
