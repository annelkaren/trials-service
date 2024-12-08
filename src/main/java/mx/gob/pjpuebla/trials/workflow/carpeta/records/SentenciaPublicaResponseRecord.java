package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.time.LocalDate;

public record SentenciaPublicaResponseRecord(
        Integer idCarpeta,
        Integer idDocumento,
        String actor,
        String demandado,
        String materia,
        String juzgado,
        String sentencia,
        String resolucion,
        LocalDate fechaResolucion
) implements Serializable {}
