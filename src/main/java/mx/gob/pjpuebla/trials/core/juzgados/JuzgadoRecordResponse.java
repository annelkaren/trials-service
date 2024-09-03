package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.Estado;

public record JuzgadoRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        String materia) {
}
