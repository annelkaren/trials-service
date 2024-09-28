package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.util.enums.Estado;

public record OficialiaMateriaRecord(
        Integer id,
        String  nombre,
        Estado  estado,
        String  materiaNombres,
        Integer materiaId,
        Integer sedeId,
        String  tipoOficialiaNombre,
        Integer tipoId,
        String  juzgadoNombre,
        Integer juzgadoId
) {}
