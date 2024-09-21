package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

public record JuzgadoRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        String materia,
        Integer maxAsignacionesRonda,
        Integer contadorAsignaciones) implements Serializable {
}
