package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.Prioridad;

import java.io.Serializable;
import java.time.LocalDateTime;

public record DocumentoBandejaRecepcionRecord(
        Integer carpetaId,
        Integer documentoId,
        String folio,
        String expediente,
        String tipoEntrada,
        String origen,
        String concepto,
        LocalDateTime fechaHoraEnvio,
        Boolean isInterno,
        Prioridad prioridad,
        Integer horas,
        Integer conceptoId
) implements Serializable {
}
