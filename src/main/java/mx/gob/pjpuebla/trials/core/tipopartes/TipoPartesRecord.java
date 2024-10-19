package mx.gob.pjpuebla.trials.core.tipopartes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoPartesRecord(
        Integer id,
        String nombre,
        String tipoJuicio
) implements Serializable {
}
