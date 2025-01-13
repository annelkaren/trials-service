package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record CarpetaResponseRecord(
        Integer idCarpeta,
        String actor,
        String demandado,
        String tipoJuicio
) implements Serializable {}
