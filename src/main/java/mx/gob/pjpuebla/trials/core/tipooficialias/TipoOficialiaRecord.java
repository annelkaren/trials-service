package mx.gob.pjpuebla.trials.core.tipooficialias;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoOficialiaRecord(
        Integer id,
        String nombre
) implements Serializable {
}
