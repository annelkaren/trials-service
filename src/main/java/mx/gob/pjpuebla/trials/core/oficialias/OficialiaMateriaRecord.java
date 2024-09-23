package mx.gob.pjpuebla.trials.core.oficialias;

public record OficialiaMateriaRecord(
        Integer id,
        String  oficialiaNombre,
        String  materiaNombres,
        Integer materiaId,
        String  tipoOficialiaNombre,
        String  juzgadoNombre,
        Integer juzgadoId
) {}
