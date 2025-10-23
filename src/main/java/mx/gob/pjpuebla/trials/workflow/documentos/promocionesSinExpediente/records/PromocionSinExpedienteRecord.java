package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;

public record PromocionSinExpedienteRecord(
    String expediente,
    Integer juzgadoId,
    TipoPromocion tipoPromocion,
    Integer year,
    List<String> anexos
) {}

