package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LenguaIndigenaRecord(
        Integer id,
        String name
) implements Serializable {
}