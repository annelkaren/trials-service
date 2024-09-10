package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.sedes.SedeRecord;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record OficialiaRecord(
        Integer id,
        Integer version,
        Estado estado,
        TipoOficialiaRecord tipo,
        String nombre,
        Integer domicilio,
        String responsable,
        SedeRecord sede
) {
}
