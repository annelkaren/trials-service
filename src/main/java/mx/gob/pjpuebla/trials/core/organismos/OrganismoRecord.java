package mx.gob.pjpuebla.trials.core.organismos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrganismoRecord(
        Integer id,
        String nombre
) implements Serializable {
}
