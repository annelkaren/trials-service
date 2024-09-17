package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.sedes.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record OficialiaRecord(
        Integer id,
        Integer version,
        String nombre,
        String responsable,
        Estado estado,
        TipoOficialiaRecord tipo,
        SedeRecordResponse sede
) {
}
