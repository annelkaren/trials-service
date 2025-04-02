package mx.gob.pjpuebla.trials.core.catalogos.sedes.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SedeRecordResponse(
        Integer id,
        String nombre,
        Estado estado
) implements Serializable {
}
