package mx.gob.pjpuebla.trials.core.personas;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonaLoginRecord(
        String username,
        String password
) implements Serializable { }
