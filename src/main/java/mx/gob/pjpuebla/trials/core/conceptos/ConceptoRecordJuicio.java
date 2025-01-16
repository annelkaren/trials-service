package mx.gob.pjpuebla.trials.core.conceptos;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConceptoRecordJuicio(
        Integer conceptoId,
        String concepto,
        String nombreTipoJuicio,
        Integer dias,
        Estado estado,
        Integer juicioId,
        Integer materia
) implements Serializable {
}
