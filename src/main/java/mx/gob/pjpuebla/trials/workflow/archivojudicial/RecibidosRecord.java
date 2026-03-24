package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public record RecibidosRecord(
        Long id,
        String juzgado,
        String expediente,
        String actor,
        String demandado,
        String tipo,
        LocalDateTime fechaRecepcion,
        Integer paquete
) {
}
