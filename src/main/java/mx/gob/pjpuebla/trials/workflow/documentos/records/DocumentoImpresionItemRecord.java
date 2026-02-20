package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoImpresionItemRecord(
        Integer documentoId,
        String folio,
        String expediente,
        String tipoEntrada,
        Boolean esPieza,
        String tipoPieza,
        LocalDateTime fechaRegistro,
        Boolean puedeImprimirSello,
        Boolean puedeImprimirCaratula
) {
}
