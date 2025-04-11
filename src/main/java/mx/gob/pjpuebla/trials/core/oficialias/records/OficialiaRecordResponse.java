package mx.gob.pjpuebla.trials.core.oficialias.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OficialiaRecordResponse(
        Integer id,
        String nombre
) implements Serializable {
}
