package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.util.List;

public record ParticipantesRecord(
        String tipo,
        List<ParticipanteDataRecord> data
) {
}
