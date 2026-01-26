package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.time.LocalDate;
import java.util.List;

public record DevolverArchivoJudicialRequest(
        List<Integer> ids,
        Boolean urgente,
        LocalDate fechaTermino
) {
}
