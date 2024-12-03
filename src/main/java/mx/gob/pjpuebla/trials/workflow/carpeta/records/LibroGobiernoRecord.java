package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.time.LocalDateTime;

public record LibroGobiernoRecord(
        Integer id,
        String numExpediente,
        LocalDateTime fechaHora,
        String tipoJuicio,
        String actor,
        String demandado
) {
}
