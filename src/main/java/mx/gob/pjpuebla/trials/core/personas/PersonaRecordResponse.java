package mx.gob.pjpuebla.trials.core.personas;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonaRecordResponse(
        Long id,
        String nombre,
        String email,
        String celular
) implements Serializable {
}
