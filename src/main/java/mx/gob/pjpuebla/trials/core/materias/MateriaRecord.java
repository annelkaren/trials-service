package mx.gob.pjpuebla.trials.core.materias;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MateriaRecord(
        Integer id,
        String nombre
) implements Serializable {
}
