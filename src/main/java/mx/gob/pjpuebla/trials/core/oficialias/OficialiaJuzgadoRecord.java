package mx.gob.pjpuebla.trials.core.oficialias;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OficialiaJuzgadoRecord(
        Integer id,
        String nombre
) implements Serializable {
    public OficialiaJuzgadoRecord(Object[] values) {
        this(
            (Integer) values[0],
            (String) values[1]
        );
    }
}
