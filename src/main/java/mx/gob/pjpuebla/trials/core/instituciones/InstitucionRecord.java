package mx.gob.pjpuebla.trials.core.instituciones;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstitucionRecord(
        Integer id,
        String nombre,
        String domicilio,
        String telefono
) implements Serializable {
}