package mx.gob.pjpuebla.trials.workflow.contadoresJuzgados;

import java.io.Serializable;

public record ContadorJuzgadoSaveRecord(
        Integer juzgadoId,
        Integer tipoJuicioId,
        Integer maxAsignaciones,
        Integer contadorAsignaciones
) implements Serializable {
}
