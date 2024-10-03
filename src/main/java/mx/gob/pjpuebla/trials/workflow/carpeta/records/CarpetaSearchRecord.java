package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record CarpetaSearchRecord(
        String numExpediente,
        Integer year,
        Integer idJuzgado
) implements Serializable {}
