package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record JuzgadoRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        String materia) {
}
