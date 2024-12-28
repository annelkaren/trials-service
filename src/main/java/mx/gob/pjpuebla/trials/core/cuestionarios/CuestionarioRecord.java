package mx.gob.pjpuebla.trials.core.cuestionarios;

import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import mx.gob.pjpuebla.trials.util.enums.TipoPregunta;

public record CuestionarioRecord(
        Integer id,
        String pregunta,
        ListCuestionario lista,
        TipoPregunta tipo
) {}
