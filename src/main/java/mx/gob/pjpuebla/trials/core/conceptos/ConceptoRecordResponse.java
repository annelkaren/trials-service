package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record ConceptoRecordResponse(
        Integer id,
        String nombre,
        Integer dias,
        Estado estado
) {
}
