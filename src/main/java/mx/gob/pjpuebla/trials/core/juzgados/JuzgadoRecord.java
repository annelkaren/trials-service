package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.Estado;

public record JuzgadoRecord(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        Integer materiaId,
        Integer sedeId) {
}
