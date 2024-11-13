package mx.gob.pjpuebla.trials.core.nacionalidades;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NacionalidadRecord(
        Integer id,
        String name
) implements Serializable {
}
