package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;

import java.util.List;

public record DocumentoRecepcionMovimientosRecord(
    List<AnexoBandejaRecepcionRecord> anexos,
    String observaciones,
    String recomendaciones
) {}
