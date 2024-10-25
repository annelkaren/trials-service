package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoBandejaRecepcionRecord(
        Integer id,
        String folio,
        String expediente,
        String tipoEntrada,
        String origen,
        String concepto,
        LocalDateTime fechaHoraEnvio,
        Boolean isInterno
) {
}
