package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDate;

public record ArchivoJudicialRecord(
        Long id,
        String juzgado,
        String tipo,
        String tipoId,
        String expediente,
        LocalDate fechaAlta,
        String anexos
) {
}
