package mx.gob.pjpuebla.trials.core.estadocivil;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstadoCivilRecord(
        Integer id,
        String nombre
) implements Serializable {

}
