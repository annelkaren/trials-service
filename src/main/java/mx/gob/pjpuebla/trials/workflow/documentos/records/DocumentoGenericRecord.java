package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

public record DocumentoGenericRecord(
    Integer id,
    TipoDocumento tipoDocumento
) {}
