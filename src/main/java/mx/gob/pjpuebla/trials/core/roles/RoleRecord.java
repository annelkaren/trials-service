package mx.gob.pjpuebla.trials.core.roles;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleRecord(
        String id,
        String name
) implements Serializable {
}
