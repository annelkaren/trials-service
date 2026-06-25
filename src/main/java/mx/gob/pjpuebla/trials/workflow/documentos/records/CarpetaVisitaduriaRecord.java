package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record CarpetaVisitaduriaRecord(
        Integer id,
        String expediente,
        LocalDateTime fechaRecepcion,
        LocalDateTime turnadoSecretario,
        String descripcion,
        String tipo,
        Integer dias
) {
}
