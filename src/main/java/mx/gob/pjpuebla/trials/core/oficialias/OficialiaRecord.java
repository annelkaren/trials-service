package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.util.Estado;

public record OficialiaRecord(
        Integer id,
        Integer version,
        Estado estado,
        Integer tipo,
        String nombre,
        Integer domicilio,
        String responsable,
        Integer sede
) {
}
