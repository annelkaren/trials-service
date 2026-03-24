package mx.gob.pjpuebla.trials.core.oficialias;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;

public record OficialiaMateriaRecord(
        Integer id,
        String  nombre,
        Estado  estado,
        String  materiaNombres,
        Object materiaId,
        Integer sedeId,
        String  tipoOficialiaNombre,
        Integer tipoId,
        String  juzgadoNombre,
        Integer juzgadoId,
        List<CarpetaCatalogoRecord> tipoDocumentos
) {}
