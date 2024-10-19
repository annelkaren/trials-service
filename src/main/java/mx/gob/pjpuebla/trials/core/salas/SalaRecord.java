package mx.gob.pjpuebla.trials.core.salas;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalaRecord(
        Integer id,
        String nombre,
        String juez,
        String juzgado,
        BloqueRecord bloque,
        Estado estado
) implements Serializable {
}
