package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.TipoConcepto;

public record DocumentoAsignadoRecord(
    Integer id,
    String expediente,
    String folio,
    String tipoEntrada,
    TipoConcepto concepto,
    LocalDateTime fechaTurnado,
    LocalDateTime fechaTermino,
    String estatus,
    String observaciones
) {

}

