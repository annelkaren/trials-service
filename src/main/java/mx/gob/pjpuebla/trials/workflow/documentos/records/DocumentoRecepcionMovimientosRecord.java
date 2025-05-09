package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;

import java.io.Serializable;
import java.util.List;

public record DocumentoRecepcionMovimientosRecord(
    Integer documentoId,
    List<AnexoBandejaRecepcionRecord> anexos,
    String observaciones,
    String recomendaciones
) implements Serializable {}
