package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.util.List;

import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord;

public record DocumentoRecepcionRecord(
    String folio,
    String expediente,
    String tipoEntrada,
    String digitalizacion,
    List<AnexoRecepcionRecord> anexos
) {}
