package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record RelacionExpedientesRecord(
        String expediente,
        String nombreJuzgado,
        String nombreTipoJuicio
) implements Serializable {
}
