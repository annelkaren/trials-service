package mx.gob.pjpuebla.trials.workflow.documentos.records;

public record DevolucionBandejasRecord(
    Integer carpetaId,
    Integer documentoId,
    String motivoDevolucion,
    String estado
) {}
