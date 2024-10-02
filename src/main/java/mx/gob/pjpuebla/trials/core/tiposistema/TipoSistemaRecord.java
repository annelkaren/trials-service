package mx.gob.pjpuebla.trials.core.tiposistema;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoSistemaRecord(
        Integer id,
        String nombre
) implements Serializable {

}
