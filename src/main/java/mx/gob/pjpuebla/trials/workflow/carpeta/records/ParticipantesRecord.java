package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.util.List;

public record ParticipantesRecord(
        String tipo,
        List<ParticipanteDataRecord> data
) implements Serializable {
}
