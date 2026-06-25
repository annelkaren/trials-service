package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoVisitaduriaRecord(
        Integer id,
        String expediente,
        Integer idCarpeta,
        LocalDateTime fechaAlta,
        String nombreCompletoPersonal,
        String emailsNotificados,
        String tipoAcuerdo
) {
}
