package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record EtapaProcesalRecord(
        Integer id,
        String etapaProcesal
) implements Serializable {
}
