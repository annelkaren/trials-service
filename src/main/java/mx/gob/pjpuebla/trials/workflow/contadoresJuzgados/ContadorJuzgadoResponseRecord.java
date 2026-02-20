package mx.gob.pjpuebla.trials.workflow.contadoresJuzgados;

import java.io.Serializable;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record ContadorJuzgadoResponseRecord(
        Integer id,
        Integer juzgadoId,
        String juzgadoNombre,
        Integer tipoJuicioId,
        String tipoJuicioNombre,
        Integer maxAsignaciones,
        Integer contadorAsignaciones,
        Estado estado
) implements Serializable {
}
