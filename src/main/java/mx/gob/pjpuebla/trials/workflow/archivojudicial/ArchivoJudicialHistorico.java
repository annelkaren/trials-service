package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import java.time.LocalDateTime;

public record ArchivoJudicialHistorico(
        Integer movimientoId,
        String tipoEntrada,
        String folio,
        String expediente,
        String estatus,
        String materia,
        LocalDateTime fechaRegistro) {
}
