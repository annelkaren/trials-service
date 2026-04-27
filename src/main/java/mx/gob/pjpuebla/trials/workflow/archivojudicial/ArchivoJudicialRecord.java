package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public record ArchivoJudicialRecord(
                Long id,
                String juzgado,
                String tipo,
                String tipoId,
                String expediente,
                LocalDateTime fechaAlta,
                String anexos,
                Integer paquete) {
}
