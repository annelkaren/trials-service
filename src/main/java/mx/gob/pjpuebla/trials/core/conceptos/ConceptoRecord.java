package mx.gob.pjpuebla.trials.core.conceptos;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.util.enums.Estado;


import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConceptoRecord(
        Integer id,
        String concepto,
        Integer dias,
        String nombreTipoJuicio,
        Estado estado
) implements Serializable {
}
