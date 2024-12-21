package mx.gob.pjpuebla.trials.workflow.transferencias.records;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAsignadoResponseRecord;
import java.util.List;
import java.time.LocalDateTime;

public record TransferenciaRecordResponse(
        Integer id,
        Long personaEntregaId,
        String personaEntrega,
        String personaEntregaPuesto,
        Long personaRecibeId,
        String personaRecibe,
        String personaRecibePuesto,
        LocalDateTime fechaTransferencia,
        Integer totalExpedientes,
        List<DocumentoAsignadoResponseRecord> expedientes,
        String uuid,
        String estatus
) {
}
