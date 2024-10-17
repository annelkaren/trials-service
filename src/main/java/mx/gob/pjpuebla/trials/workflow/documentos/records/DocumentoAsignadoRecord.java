package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

public record DocumentoAsignadoRecord(
    Integer id,
    String expediente,
    String folioCarpeta,
    String folioDocumento,
    TipoCarpeta tipoCarpeta,
    TipoDocumento tipoDocumento,
    Concepto concepto,
    LocalDateTime fechaTurnado,
    EstadoCarpeta estatus,
    String observaciones
) {

}

