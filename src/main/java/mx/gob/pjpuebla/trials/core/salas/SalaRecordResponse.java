package mx.gob.pjpuebla.trials.core.salas;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.core.personas.JuezRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalaRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        Integer version,
        JuezRecord juez,
        JuzgadoRecordItem juzgado,
        BloqueRecord bloque
) implements Serializable {
}
