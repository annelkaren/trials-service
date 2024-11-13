package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.time.LocalDateTime;
import java.util.List;

public record InfoExpedienteRecord(
        String expediente,
        String tipoJuicio,
        String tipoCausa,
        String juezAsignado,
        LocalDateTime fechaPresentacion,
        String asunto,
        String tipoProcedimiento,
        String rubros,
        String etapaProcesal,
        List<ParticipantesRecord> participantes,
        String razonDevolucion
) {
}
