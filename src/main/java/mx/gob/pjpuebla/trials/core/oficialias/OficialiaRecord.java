package mx.gob.pjpuebla.trials.core.oficialias;

import com.fasterxml.jackson.annotation.JsonInclude;

import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OficialiaRecord(
        Integer id,
        Integer version,
        String nombre,
        String responsable,
        Estado estado,
        TipoOficialiaRecord tipo,
        SedeRecordResponse sede
) implements Serializable {
}
