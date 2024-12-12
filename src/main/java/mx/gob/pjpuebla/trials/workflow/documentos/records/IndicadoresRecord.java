package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;

public record IndicadoresRecord(
        Integer indicador1,
        Integer indicador2,
        Integer indicador3,
        Integer indicador4
) implements Serializable {
}
