package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

public record JuzgadoRecord(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        Integer materiaId,
        Integer sedeId,
        Integer maxAsignacionesRonda,
        Integer contadorAsignaciones) implements Serializable {
}
