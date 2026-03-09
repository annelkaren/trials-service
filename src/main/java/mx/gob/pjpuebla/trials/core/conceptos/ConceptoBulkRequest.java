package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;
import java.util.List;

public record ConceptoBulkRequest(
        Integer id,
        String nombre,
        Integer dias,
        Estado estado,
        List<TipoJuicioIdRequest> tipoJuicios
) implements Serializable {
}
