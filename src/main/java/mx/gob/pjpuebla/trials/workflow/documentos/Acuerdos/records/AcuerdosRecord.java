package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record AcuerdosRecord(
   Integer numeroAcuerdo,
   LocalDate fechaPublicacion,
   String resumen,
   EstadoCarpeta estatus,
   String extractoSentencia
) { }
