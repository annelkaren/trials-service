package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecepcionRecord;

public record DocumentoRecepcionRecord(
    String folio,
    String expediente,
    String tipoEntrada,
    String digitalizacion,
    String origen,
    List<AnexoRecepcionRecord> anexos
) implements Serializable {}
