package mx.gob.pjpuebla.trials.core.juzgados;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

/**
 * Objeto usado para mostrar una vista corta del Juzgado, usado normalmente en una lista
 * @param id
 * @param nombre
 * @param estado
 * @param materia
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JuzgadoRecordItem(
        Integer id,
        String nombre,
        Estado estado,
        String materia
) implements Serializable {
}
